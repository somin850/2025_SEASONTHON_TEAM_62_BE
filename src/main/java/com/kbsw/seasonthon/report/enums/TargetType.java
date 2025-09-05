package com.kbsw.seasonthon.report.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TargetType {
    ROUTE("경로"),
    LOCATION("위치"),
    HAZARD("위험요소");

    private final String description;
}
