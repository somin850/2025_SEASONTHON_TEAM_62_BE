package com.kbsw.seasonthon.crew.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ParticipantStatus {
    APPLIED("신청됨"),
    APPROVED("승인됨"),
    REJECTED("거절됨");

    private final String description;
}
