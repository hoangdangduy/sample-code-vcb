package com.vcb.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    EMPLOYEE_NOT_FOUND("EMP-001", HttpStatus.NOT_FOUND, "error.employee.not_found"),
    EMPLOYEE_ALREADY_EXISTS("EMP-002", HttpStatus.CONFLICT, "error.employee.already_exists"),
    EMPLOYEE_SAVE_FAILED("EMP-003", HttpStatus.BAD_REQUEST, "error.employee.save_failed"),
    EMPLOYEE_DELETE_FAILED("EMP-004", HttpStatus.BAD_REQUEST, "error.employee.delete_failed"),

    MISSING_HANDLE_SERVER_ERROR("SYS-001", HttpStatus.BAD_REQUEST, "error.internal");

    private final String code;
    private final HttpStatus httpStatus;
    private final String messageKey;
}
