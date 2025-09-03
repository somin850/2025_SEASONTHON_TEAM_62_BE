package com.kbsw.seasonthon.security.oauth2.enums;

import java.util.Arrays;

public enum ProviderType {
    KAKAO,
    NAVER,
    GOOGLE;

    public static ProviderType from(String provider) {
        String upperCastedProvider = provider.toUpperCase();

        return Arrays.stream(ProviderType.values())
                .filter(item -> item.name().equals(upperCastedProvider))
                .findFirst()
                .orElseThrow();
    }


}
