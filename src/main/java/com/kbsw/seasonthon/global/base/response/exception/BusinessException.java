package com.kbsw.seasonthon.global.base.response.exception;

import lombok.Getter;

/** 비즈니스 규칙 위반 시 사용하는 런타임 예외 */
@Getter
public class BusinessException extends RuntimeException {

    private final ExceptionType exceptionType;

    public BusinessException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }

    public BusinessException(ExceptionType exceptionType, String detailMessage) {
        super(detailMessage);
        this.exceptionType = exceptionType;
    }

    public BusinessException(ExceptionType exceptionType, Throwable cause) {
        super(exceptionType.getMessage(), cause);
        this.exceptionType = exceptionType;
    }

    public BusinessException(ExceptionType exceptionType, String detailMessage, Throwable cause) {
        super(detailMessage, cause);
        this.exceptionType = exceptionType;
    }
}