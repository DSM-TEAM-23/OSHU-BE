package com.dsm.oshu;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class OshuBeApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private S3Client s3Client;

    @Test
    void contextLoads() {
    }

    @Test
    void mapStoresIsPublic() throws Exception {
        mockMvc.perform(get("/stores/map")
                        .param("latitude", "36.3622").param("longitude", "127.3449"))
                .andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
    }

    @Test
    void corsPreflightAllowsVercelFrontend() throws Exception {
        String origin = "https://oshu-fe.vercel.app";
        mockMvc.perform(options("/stores")
                        .header("Origin", origin)
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", origin));
    }

    @Test
    void ownerStoreCreationRequiresBearerToken() throws Exception {
        String payload = """
                {"name":"테스트 가게","category":"카페","address":"유성구"}
                """;
        mockMvc.perform(post("/owner/stores").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isForbidden());
    }

    @Test
    void loginTokenContainsOwnerRoleAndAllowsOwnerStoresAccess() throws Exception {
        signUp("test-owner", "password123!");

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"loginId":"test-owner","password":"password123!"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isString())
                .andReturn();

        JsonNode body = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String accessToken = body.get("accessToken").asText();
        String[] tokenParts = accessToken.split("\\.");
        String payloadJson = new String(java.util.Base64.getUrlDecoder().decode(tokenParts[1]), java.nio.charset.StandardCharsets.UTF_8);
        JsonNode payload = objectMapper.readTree(payloadJson);

        org.assertj.core.api.Assertions.assertThat(payload.get("role").asText()).isEqualTo("OWNER");
        org.assertj.core.api.Assertions.assertThat(payload.get("authorities").get(0).asText()).isEqualTo("ROLE_OWNER");

        mockMvc.perform(get("/owner/stores")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void ownerStoreCreationAcceptsKoreanCategory() throws Exception {
        signUp("store-owner", "password123!");
        String accessToken = login("store-owner", "password123!");
        String payload = """
                {"name":"테스트 카페","category":"카페","address":"유성구"}
                """;
        mockMvc.perform(post("/owner/stores")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.category").value("카페"))
                .andExpect(jsonPath("$.latitude").value(36.3628))
                .andExpect(jsonPath("$.longitude").value(127.3441));
    }

    @Test
    void ownerStoreCreationAcceptsCustomCategory() throws Exception {
        signUp("custom-owner", "password123!");
        String accessToken = login("custom-owner", "password123!");
        String payload = """
                {"name":"테스트 꽃집","category":"기타","customCategory":"꽃집","address":"유성구"}
                """;
        mockMvc.perform(post("/owner/stores")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.category").value("꽃집"));
    }

    @Test
    void publicInquiryCreationIsVisibleToStoreOwner() throws Exception {
        signUp("inquiry-owner", "password123!");
        String accessToken = login("inquiry-owner", "password123!");
        String storePayload = """
                {"name":"문의 테스트 가게","category":"카페","address":"유성구"}
                """;
        MvcResult storeResult = mockMvc.perform(post("/owner/stores")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(storePayload))
                .andExpect(status().isCreated())
                .andReturn();
        Long storeId = objectMapper.readTree(storeResult.getResponse().getContentAsString()).get("storeId").asLong();

        String inquiryPayload = """
                {"title":"단체 예약 문의","content":"이번 주 금요일 저녁 예약이 가능한가요?","name":"김유저","number":"010-1234-5678"}
                """;

        mockMvc.perform(post("/inquiry/store/" + storeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inquiryPayload))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/inquiry/store/" + storeId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("단체 예약 문의"))
                .andExpect(jsonPath("$[0].name").value("김유저"))
                .andExpect(jsonPath("$[0].number").value("010-1234-5678"));
    }

    @Test
    void ownerImageUploadReturnsPublicPath() throws Exception {
        signUp("upload-owner", "password123!");
        String accessToken = login("upload-owner", "password123!");
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(null);

        MockMultipartFile image = new MockMultipartFile(
                "image",
                "test.png",
                MediaType.IMAGE_PNG_VALUE,
                "png".getBytes());

        mockMvc.perform(multipart("/owner/uploads/images")
                        .file(image)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.imageUrl").value(org.hamcrest.Matchers.startsWith("/uploads/")));
    }

    @Test
    void uploadedImageIsServedThroughPublicEndpoint() throws Exception {
        GetObjectResponse response = GetObjectResponse.builder()
                .contentType(MediaType.IMAGE_PNG_VALUE)
                .build();
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class)))
                .thenReturn(ResponseBytes.fromByteArray(response, "png".getBytes()));

        mockMvc.perform(get("/uploads/test.png"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.IMAGE_PNG_VALUE))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().bytes("png".getBytes()));
    }

    private void signUp(String loginId, String password) throws Exception {
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"loginId":"%s","password":"%s"}
                                """.formatted(loginId, password)))
                .andExpect(status().isCreated());
    }

    private String login(String loginId, String password) throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"loginId":"%s","password":"%s"}
                                """.formatted(loginId, password)))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("accessToken").asText();
    }
}
