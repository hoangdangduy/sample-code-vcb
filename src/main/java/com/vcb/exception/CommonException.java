package com.vcb.exception;

import lombok.Getter;

@Getter
public abstract class CommonException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object[] args;

    protected CommonException(ErrorCode errorCode, Object... args) {
        super(errorCode.getCode());
        this.errorCode = errorCode;
        this.args = args;
    }
}
