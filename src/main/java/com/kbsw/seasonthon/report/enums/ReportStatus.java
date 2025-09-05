package com.kbsw.seasonthon.report.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportStatus {
    OPEN("처리 대기중"),
    RESOLVED("처리 완료"),
    REJECTED("신고 반려");

    private final String description;
}
