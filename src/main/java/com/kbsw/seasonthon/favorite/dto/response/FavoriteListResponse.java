package com.kbsw.seasonthon.favorite.dto.response;

import com.kbsw.seasonthon.favorite.entity.Favorite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteListResponse {

    private Long id;
    private String name;
    private Integer distanceM;
    private Integer durationS;
    private LocalDateTime createdAt;

    public static FavoriteListResponse from(Favorite favorite) {
        return FavoriteListResponse.builder()
                .id(favorite.getId())
                .name(favorite.getName())
                .distanceM(favorite.getDistanceM())
                .durationS(favorite.getDurationS())
                .createdAt(favorite.getCreatedAt())
                .build();
    }
}
