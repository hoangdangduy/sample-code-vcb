package com.vcb.exception;

public class EmployeeSaveException extends CommonException {

    public EmployeeSaveException(ErrorCode errorCode) {
        super(errorCode);
    }
}
