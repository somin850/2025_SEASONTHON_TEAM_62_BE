package com.kbsw.seasonthon.crew.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CrewCreateRequest {
    private String title;
    private String description;
    private Integer maxParticipants;
    private String routeId;
    private List<String> tags;
    private String startLocation;        // 시작 위치 (예: "경북대학교 정문")
    private String pace;                 // 페이스 (예: "6'30\"/km")
    private LocalDateTime startTime;     // 시작 시간
}


