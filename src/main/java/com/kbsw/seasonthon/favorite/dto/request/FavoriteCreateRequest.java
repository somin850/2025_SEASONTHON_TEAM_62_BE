package com.kbsw.seasonthon.favorite.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteCreateRequest {

    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;

    @NotBlank(message = "즐겨찾기 이름은 필수입니다.")
    @Size(max = 50, message = "즐겨찾기 이름은 50자 이하여야 합니다.")
    private String name;

    private List<List<Double>> waypoints;

    private String savedPolyline;

    private Integer distanceM;

    private Integer durationS;
}
