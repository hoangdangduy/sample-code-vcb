package com.vcb.model.dto;

import lombok.Data;

@Data
public class EmployeeRequest {
    private String name;
    private int age;
    private String address;
    private String username;
}
