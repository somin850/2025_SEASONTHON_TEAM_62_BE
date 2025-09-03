package com.kbsw.seasonthon.recommend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbsw.seasonthon.recommend.dto.request.ListingReviewRequest;
import com.kbsw.seasonthon.recommend.dto.response.ListingReviewResponse;
import com.kbsw.seasonthon.recommend.llm.LlmClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LlmAdviceService {

    private final LlmClient llmClient;
    private final ObjectMapper om = new ObjectMapper();

    public ListingReviewResponse getAdviceByLlm(ListingReviewRequest r) {
        String prompt = buildAdvicePrompt(r);

        try {
            String raw = llmClient.complete(prompt);
            JsonNode root = om.readTree(raw);
            String advice = root.path("advice").asText();
            if (advice == null || advice.isBlank()) {
                throw new IllegalArgumentException("LLM 응답에 advice 없음");
            }
            return ListingReviewResponse.builder()
                    .advice(advice)
                    .fromLlm(true)
                    .build();
        } catch (Exception e) {
            String fallback = fallbackAdvice(r);
            return ListingReviewResponse.builder()
                    .advice(fallback)
                    .fromLlm(false)
                    .build();
        }
    }

    private String buildAdvicePrompt(ListingReviewRequest r) {
        return """
        당신은 기상 컨설턴트입니다.
        아래 수치를 반영해 한국어 한 줄 조언을 주세요.
        반드시 JSON만 출력하세요: { "advice": "..." }

        입력:
        - 날씨코드: %s
        - 기온(℃): %s
        - 습도(%%): %s
        - 미세먼지 PM10(µg/m³): %s
        - 강수확률(%%): %s
        """.formatted(
                toStr(r.getWeather()),
                toStr(r.getTemperature()),
                toStr(r.getHumidity()),
                toStr(r.getDust()),
                toStr(r.getRainfall())
        );
    }

    private String toStr(Object v){ return v==null? "N/A": String.valueOf(v); }

    private String fallbackAdvice(ListingReviewRequest r) {
        if (r.getRainfall() != null && r.getRainfall() > 50) return "비 올 확률이 높으니 우산을 챙기세요.";
        if (r.getDust() != null && r.getDust() > 80) return "미세먼지가 나쁘니 마스크를 착용하세요.";
        if (r.getTemperature() != null && r.getTemperature() > 30) return "더운 날씨이니 수분 섭취를 충분히 하세요.";
        if (r.getTemperature() != null && r.getTemperature() < 5)  return "쌀쌀하니 겹쳐 입어 체온을 유지하세요.";
        return "무리 없는 날씨예요. 컨디션에 맞춰 활동해 보세요.";
    }
}