package com.kbsw.seasonthon.crew.dto.response;

import com.kbsw.seasonthon.crew.enums.ParticipantStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CrewApplyResponse {
    private ParticipantStatus status;
}
