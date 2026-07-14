package com.dsm.oshu.recommendation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Locale;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class ClaudeDiscountRecommendationClient {
    private static final String ANTHROPIC_VERSION = "2023-06-01";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;

    public ClaudeDiscountRecommendationClient(
            ObjectMapper objectMapper,
            @Value("${oshu.anthropic.api-key:}") String apiKey,
            @Value("${oshu.anthropic.model:claude-haiku-4-5-20251001}") String model,
            @Value("${oshu.anthropic.base-url:https://api.anthropic.com/v1}") String baseUrl) {
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public AiDiscountRecommendation recommend(String statisticsJson) {
        if (apiKey.isBlank()) {
            throw new IllegalStateException("ANTHROPIC_API_KEY가 설정되지 않았습니다.");
        }

        String systemPrompt = "너는 소상공인 할인 시간대 분석가다. 제공된 집계 데이터만 사용한다. "
                + "주문 평균이 낮은 요일과 시간대를 골라 1~2시간 할인과 5~50% 할인율을 추천한다. "
                + "분석 기간이 4주보다 짧으면 reason에 데이터 기간이 짧다는 점을 한국어로 명시한다. "
                + "반드시 아래 JSON 객체만 반환하고 Markdown 코드 블록은 사용하지 마라. "
                + "{\"recommendedDay\":\"월요일|화요일|수요일|목요일|금요일|토요일|일요일\","
                + "\"startHour\":0~23 정수,\"endHour\":1~24 정수,\"discountRate\":5~50 정수,"
                + "\"reason\":\"한국어 설명\"}";
        Map<String, Object> request = Map.of(
                "model", model,
                "max_tokens", 400,
                "system", systemPrompt,
                "messages", List.of(Map.of(
                        "role", "user",
                        "content", "가게의 요일별 시간대 주문량 집계 데이터는 다음과 같다.\n" + statisticsJson)));

        try {
            JsonNode response = restClient.post()
                    .uri("messages")
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", ANTHROPIC_VERSION)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(JsonNode.class);
            String content = response == null ? null : response.at("/content/0/text").asText(null);
            if (content == null || content.isBlank()) {
                throw new IllegalStateException("AI 추천 결과를 받지 못했습니다. 잠시 후 다시 시도해주세요.");
            }
            return objectMapper.readValue(content, AiDiscountRecommendation.class);
        } catch (RestClientResponseException exception) {
            throw new IllegalStateException(resolveAnthropicErrorMessage(exception), exception);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("AI 추천 응답 형식이 올바르지 않습니다. 잠시 후 다시 시도해주세요.", exception);
        }
    }

    private String resolveAnthropicErrorMessage(RestClientResponseException exception) {
        String responseBody = exception.getResponseBodyAsString();
        if (responseBody != null && !responseBody.isBlank()) {
            try {
                JsonNode errorNode = objectMapper.readTree(responseBody).path("error");
                String type = errorNode.path("type").asText("");
                String message = errorNode.path("message").asText("");
                if ("not_found_error".equals(type)
                        && message.toLowerCase(Locale.ROOT).contains("model")) {
                    return "AI 추천 모델 설정이 올바르지 않습니다. 서버 모델명을 확인해주세요.";
                }
            } catch (JsonProcessingException ignored) {
                // Fall back to the generic message below when the upstream body is not JSON.
            }
        }
        return "AI 추천 서버 호출에 실패했습니다. 잠시 후 다시 시도해주세요. ("
                + exception.getStatusCode().value() + ")";
    }
}
