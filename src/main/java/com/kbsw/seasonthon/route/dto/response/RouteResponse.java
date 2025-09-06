package com.kbsw.seasonthon.route.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RouteResponse {
    private String routeId;
    private String type;
    private Double distanceKm;
    private Integer safetyScore;
    private Integer durationMin;
    private List<List<Double>> waypoints;
}


