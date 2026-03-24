package com.vcb.exception;

public class EmployeeNotFound extends CommonException {

    public EmployeeNotFound(String username) {
        super(ErrorCode.EMPLOYEE_NOT_FOUND, username);
    }
}
