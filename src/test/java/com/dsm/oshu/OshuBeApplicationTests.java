package com.dsm.oshu;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
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
import org.springframework.web.util.UriComponentsBuilder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@SpringBootTest(properties = {
        "spring.security.oauth2.client.registration.google.client-id=test-google-client.apps.googleusercontent.com",
        "spring.security.oauth2.client.registration.google.client-secret=test-google-client-secret",
        "oshu.public-data.service-key="
})
@AutoConfigureMockMvc
class OshuBeApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private S3Client s3Client;

    @MockBean
    private com.dsm.oshu.recommendation.service.ClaudeDiscountRecommendationClient claudeDiscountRecommendationClient;

    @Autowired
    private com.dsm.oshu.auth.service.AuthService authService;

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
    void mapStoresIncludesActiveTimeSaleScheduleAndDiscountRate() throws Exception {
        signUp("map-time-sale-owner", "password123!");
        String accessToken = login("map-time-sale-owner", "password123!");
        MvcResult storeResult = mockMvc.perform(post("/owner/stores")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"지도 할인 테스트","category":"카페","address":"유성구"}
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        long storeId = objectMapper.readTree(storeResult.getResponse().getContentAsString()).get("storeId").asLong();
        LocalDateTime now = LocalDateTime.now();

        mockMvc.perform(post("/owner/stores/" + storeId + "/time-sales")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productName":"할인 커피","originalPrice":5000,"salePrice":3500,
                                "startAt":"%s","endAt":"%s"}
                                """.formatted(now.minusMinutes(5), now.plusHours(1))))
                .andExpect(status().isCreated());

        MvcResult mapResult = mockMvc.perform(get("/stores/map")
                        .param("latitude", "36.3628").param("longitude", "127.3441"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode mappedStores = objectMapper.readTree(mapResult.getResponse().getContentAsString());
        JsonNode mappedStore = null;
        for (JsonNode store : mappedStores) {
            if (storeId == store.get("storeId").asLong()) {
                mappedStore = store;
                break;
            }
        }

        assertNotNull(mappedStore);
        assertEquals(true, mappedStore.get("timeSaleActive").asBoolean());
        assertEquals(30, mappedStore.get("discountRate").asInt());
        assertEquals(true, mappedStore.get("timeSaleStartAt").isTextual());
        assertEquals(true, mappedStore.get("timeSaleEndAt").isTextual());
    }

    @Test
    void mapStoresIncludesUpcomingTimeSaleScheduleAndDiscountRate() throws Exception {
        signUp("upcoming-map-time-sale-owner", "password123!");
        String accessToken = login("upcoming-map-time-sale-owner", "password123!");
        MvcResult storeResult = mockMvc.perform(post("/owner/stores")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"예정 지도 할인 테스트","category":"카페","address":"유성구"}
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        long storeId = objectMapper.readTree(storeResult.getResponse().getContentAsString()).get("storeId").asLong();
        LocalDateTime now = LocalDateTime.now();

        mockMvc.perform(post("/owner/stores/" + storeId + "/time-sales")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productName":"예정 할인 커피","originalPrice":6000,"salePrice":4500,
                                "startAt":"%s","endAt":"%s"}
                                """.formatted(now.plusHours(1), now.plusHours(2))))
                .andExpect(status().isCreated());

        MvcResult mapResult = mockMvc.perform(get("/stores/map")
                        .param("latitude", "36.3628").param("longitude", "127.3441"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode mappedStores = objectMapper.readTree(mapResult.getResponse().getContentAsString());
        JsonNode mappedStore = null;
        for (JsonNode store : mappedStores) {
            if (storeId == store.get("storeId").asLong()) {
                mappedStore = store;
                break;
            }
        }

        assertNotNull(mappedStore);
        assertEquals(false, mappedStore.get("timeSaleActive").asBoolean());
        assertEquals(25, mappedStore.get("discountRate").asInt());
        assertEquals(true, mappedStore.get("timeSaleStartAt").isTextual());
        assertEquals(true, mappedStore.get("timeSaleEndAt").isTextual());
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
    void googleLoginCodeCanBeExchangedOnlyOnce() throws Exception {
        String code = authService.createGoogleLoginCode("google-subject-123", "google-user@example.com");

        mockMvc.perform(post("/auth/google/exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code":"%s"}
                                """.formatted(code)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));

        mockMvc.perform(post("/auth/google/exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code":"%s"}
                                """.formatted(code)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void googleLoginStartRedirectsToGoogleAuthorizationPage() throws Exception {
        MvcResult result = mockMvc.perform(get("/oauth2/authorization/google")
                        .secure(true)
                        .header("Host", "api.oshu.example")
                        .header("X-Forwarded-Proto", "https"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", org.hamcrest.Matchers.startsWith(
                        "https://accounts.google.com/o/oauth2/v2/auth")))
                .andReturn();

        String googleAuthorizationUri = result.getResponse().getHeader("Location");
        String callbackUri = UriComponentsBuilder.fromUriString(googleAuthorizationUri)
                .build()
                .getQueryParams()
                .getFirst("redirect_uri");
        assertEquals("https://api.oshu.example/login/oauth2/code/google", callbackUri);
    }

    @Test
    void ownerCanReceiveAiDiscountRecommendationFromDailyOrderStatistics() throws Exception {
        signUp("recommendation-owner", "password123!");
        String accessToken = login("recommendation-owner", "password123!");
        MvcResult storeResult = mockMvc.perform(post("/owner/stores")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"추천 테스트 가게","category":"카페","address":"유성구","openingHours":"09:00 - 21:00"}
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        long storeId = objectMapper.readTree(storeResult.getResponse().getContentAsString()).get("storeId").asLong();
        String orderDate = "2026-07-14";

        mockMvc.perform(post("/owner/stores/" + storeId + "/order-statistics")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"orderDate":"%s","hourlyOrderCounts":[
                                  {"hour":12,"orderCount":12},
                                  {"hour":15,"orderCount":1},
                                  {"hour":16,"orderCount":0}
                                ]}
                                """.formatted(orderDate)))
                .andExpect(status().isNoContent());

        when(claudeDiscountRecommendationClient.recommend(anyString()))
                .thenReturn(new com.dsm.oshu.recommendation.service.AiDiscountRecommendation(
                        "화요일", 15, 17, 15, "분석된 주문 데이터에서 15~17시 주문량이 가장 낮습니다."));

        mockMvc.perform(get("/owner/stores/" + storeId + "/discount-recommendations")
                        .param("orderDate", orderDate)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recommendedDay").value("화요일"))
                .andExpect(jsonPath("$.startHour").value(15))
                .andExpect(jsonPath("$.endHour").value(17))
                .andExpect(jsonPath("$.discountRate").value(15));

        verify(claudeDiscountRecommendationClient).recommend(anyString());
    }

    @Test
    void orderStatisticsOutsideOpeningHoursAreRejected() throws Exception {
        signUp("schedule-owner", "password123!");
        String accessToken = login("schedule-owner", "password123!");
        MvcResult storeResult = mockMvc.perform(post("/owner/stores")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"운영시간 테스트 가게","category":"카페","address":"유성구","openingHours":"09:00 - 21:00"}
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        long storeId = objectMapper.readTree(storeResult.getResponse().getContentAsString()).get("storeId").asLong();

        mockMvc.perform(post("/owner/stores/" + storeId + "/order-statistics")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"orderDate":"2026-07-15","hourlyOrderCounts":[
                                  {"hour":8,"orderCount":3},
                                  {"hour":10,"orderCount":5}
                                ]}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("가게 운영시간에 해당하는 시간대만 주문 데이터를 저장할 수 있습니다."));
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
