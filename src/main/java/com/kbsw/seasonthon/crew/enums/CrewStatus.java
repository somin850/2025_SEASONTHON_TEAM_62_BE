package com.kbsw.seasonthon.crew.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CrewStatus {
    OPEN("모집중"),
    CLOSED("모집완료"),
    CANCELLED("취소됨");

    private final String description;
}
