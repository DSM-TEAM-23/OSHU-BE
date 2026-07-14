package com.dsm.oshu;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class OshuBeApplicationTests {
    @Autowired
    private MockMvc mockMvc;

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
    void ownerStoreCreationRequiresBearerToken() throws Exception {
        String payload = """
                {"name":"테스트 가게","category":"카페","address":"유성구","latitude":36.36,"longitude":127.34}
                """;
        mockMvc.perform(post("/owner/stores").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isForbidden());
    }

    @Test
    void ownerStoreCreationAcceptsKoreanCategory() throws Exception {
        String payload = """
                {"name":"테스트 카페","category":"카페","address":"유성구","latitude":36.36,"longitude":127.34}
                """;
        mockMvc.perform(post("/owner/stores")
                        .header("Authorization", "Bearer oshu-owner-dev-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.category").value("카페"));
    }
}
