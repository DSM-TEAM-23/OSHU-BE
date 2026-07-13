package com.dsm.oshu.store.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

/** Adapter for the Small Enterprise and Market Service store-information API. */
@Component
public class PublicStoreDataClient {
    private final PublicDataProperties properties;
    private final ObjectMapper objectMapper;

    public PublicStoreDataClient(PublicDataProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    public List<PublicStore> findStoresInRadius(double latitude, double longitude, int radius) {
        if (!StringUtils.hasText(properties.getServiceKey())) return List.of();
        try {
            String body = RestClient.create(properties.getBaseUrl())
                    .get()
                    .uri(builder -> builder.path("/storeListInRadius")
                            .queryParam("serviceKey", properties.getServiceKey())
                            .queryParam("type", "json")
                            .queryParam("cx", longitude)
                            .queryParam("cy", latitude)
                            .queryParam("radius", radius)
                            .build())
                    .retrieve().body(String.class);
            return parse(body);
        } catch (Exception ignored) {
            // The local store data remains available while the public API is unavailable.
            return List.of();
        }
    }

    private List<PublicStore> parse(String body) throws Exception {
        JsonNode root = objectMapper.readTree(body);
        JsonNode items = root.path("body").path("items").path("item");
        if (!items.isArray()) return List.of();
        List<PublicStore> stores = new ArrayList<>();
        for (JsonNode item : items) {
            String name = item.path("bizesNm").asText("이름 미등록 가게");
            String category = item.path("indsLclsNm").asText("기타");
            String address = item.path("rdnmAdr").asText(item.path("lnbrAdr").asText("주소 미등록"));
            double lat = item.path("lat").asDouble(Double.NaN);
            double lon = item.path("lon").asDouble(Double.NaN);
            if (!Double.isNaN(lat) && !Double.isNaN(lon)) {
                stores.add(new PublicStore(name, category, address, lat, lon));
            }
        }
        return stores;
    }
}
