package com.vcb.service;

import com.vcb.model.dto.EmployeeRequest;
import com.vcb.model.dto.EmployeeResponse;
import java.util.List;

public interface EmployeeService {
    List<EmployeeResponse> getAllEmployees();
    EmployeeResponse getEmployeeByUsername(String username);
    void addEmployee(EmployeeRequest dto);
    void deleteEmployeeBy(String username);
}
