package com.vcb.controller;

import com.vcb.model.dto.EmployeeResponse;
import com.vcb.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<EmployeeResponse> getProfile(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(employeeService.getEmployeeByCode(getClaimSafe(jwt)));
    }

    private String getClaimSafe(Jwt jwt) {
        Object value = jwt.getClaim("preferred_username");
        return value != null ? value.toString() : "N/A";
    }
}
