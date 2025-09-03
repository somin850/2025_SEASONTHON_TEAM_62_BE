package com.kbsw.seasonthon.recommend.service;

import com.kbsw.seasonthon.recommend.dto.request.LiveAdviceRequest;
import com.kbsw.seasonthon.recommend.dto.request.ListingReviewRequest;
import com.kbsw.seasonthon.recommend.dto.response.ListingReviewResponse;
import com.kbsw.seasonthon.recommend.external.OpenMeteoProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecommendService {

    private final OpenMeteoProvider openMeteoProvider;
    private final LlmAdviceService llmAdviceService; // 아래에서 분리한 LLM 호출부

    /** 기존에 작성해둔 LLM 한줄 조언 로직을 분리해서 사용 */
    @Transactional(readOnly = true)
    public ListingReviewResponse listingReview(ListingReviewRequest r) {
        return llmAdviceService.getAdviceByLlm(r);
    }

    /** 좌표 → 실시간 데이터 조회 → ListingReviewRequest 생성 → LLM 조언 */
    @Transactional(readOnly = true)
    public ListingReviewResponse liveListingReview(LiveAdviceRequest req) {
        var snap = openMeteoProvider.fetch(req.getLat(), req.getLon());

        ListingReviewRequest lr = ListingReviewRequest.builder()
                .Weather(snap.getWeatherCode())                         // weather_code 그대로 사용
                .Temperature(snap.getTemperature())
                .Humidity(snap.getHumidity())
                .Dust(snap.getDust())
                .Rainfall(snap.getRainfallProbability())
                .build();

        return llmAdviceService.getAdviceByLlm(lr);
    }
}