package com.kbsw.seasonthon.global.base.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public final class FailedResponseBody extends ResponseBody<Void> {
    private String msg;
    private boolean success = false;

    public FailedResponseBody(String code, String msg) {
        this.setCode(code);
        this.msg = msg;
        this.success = false;
    }
}