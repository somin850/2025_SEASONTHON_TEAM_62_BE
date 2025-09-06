package com.kbsw.seasonthon.crew.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SafetyLevel {
    SAFE("안전", 80, 100),
    MEDIUM("중간", 50, 79),
    UNSAFE("불안", 0, 49);

    private final String description;
    private final int minScore;
    private final int maxScore;

    public static SafetyLevel fromScore(int score) {
        for (SafetyLevel level : values()) {
            if (score >= level.minScore && score <= level.maxScore) {
                return level;
            }
        }
        return UNSAFE; // 기본값
    }
}


