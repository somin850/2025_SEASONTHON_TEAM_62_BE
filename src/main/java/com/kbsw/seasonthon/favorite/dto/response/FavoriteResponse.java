package com.kbsw.seasonthon.favorite.dto.response;

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
public class FavoriteResponse {

    private Long id;
    private String name;
    private List<String> waypoints;
    private String savedPolyline;
    private Integer distanceM;
    private Integer durationS;
    private LocalDateTime createdAt;

    public static FavoriteResponse from(Favorite favorite) {
        return FavoriteResponse.builder()
                .id(favorite.getId())
                .name(favorite.getName())
                .waypoints(favorite.getWaypoints())
                .savedPolyline(favorite.getSavedPolyline())
                .distanceM(favorite.getDistanceM())
                .durationS(favorite.getDurationS())
                .createdAt(favorite.getCreatedAt())
                .build();
    }
}
