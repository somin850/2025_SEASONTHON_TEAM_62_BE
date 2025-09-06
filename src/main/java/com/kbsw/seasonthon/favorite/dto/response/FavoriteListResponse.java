package com.kbsw.seasonthon.favorite.dto.response;

import com.kbsw.seasonthon.crew.enums.SafetyLevel;
import com.kbsw.seasonthon.favorite.entity.Favorite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteListResponse {

    private Long id;
    private String name;
    private Integer distanceM;
    private Integer durationS;
    private Integer safetyScore;
    private SafetyLevel safetyLevel;
    private List<String> tags;
    private LocalDateTime createdAt;

    public static FavoriteListResponse from(Favorite favorite) {
        return FavoriteListResponse.builder()
                .id(favorite.getId())
                .name(favorite.getName())
                .distanceM(favorite.getDistanceM())
                .durationS(favorite.getDurationS())
                .safetyScore(favorite.getSafetyScore())
                .safetyLevel(favorite.getSafetyLevel())
                .tags(favorite.getTags())
                .createdAt(favorite.getCreatedAt())
                .build();
    }
}
