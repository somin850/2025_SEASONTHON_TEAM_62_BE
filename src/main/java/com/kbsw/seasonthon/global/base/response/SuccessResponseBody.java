package com.kbsw.seasonthon.global.base.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public final class SuccessResponseBody<T> extends ResponseBody<T> {
    private T data;
    private boolean success = true;

    public SuccessResponseBody(T data) {
        this.data = data;
        this.success = true;
    }
}