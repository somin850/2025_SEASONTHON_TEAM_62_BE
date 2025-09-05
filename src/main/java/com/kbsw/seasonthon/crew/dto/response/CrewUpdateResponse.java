package com.kbsw.seasonthon.crew.dto.response;

import com.kbsw.seasonthon.crew.enums.CrewStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CrewUpdateResponse {
    private Long id;
    private CrewStatus status;
    private LocalDateTime updatedAt;
}


