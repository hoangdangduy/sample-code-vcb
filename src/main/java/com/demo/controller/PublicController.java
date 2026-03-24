package com.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    @GetMapping("/info")
    public Map<String, Object> info() {
        return Map.of(
            "app", "IAM & CIAM Demo",
            "version", "1.0.0",
            "description", "Spring Boot tích hợp IAM (Keycloak) + CIAM (Auth0)",
            "endpoints", Map.of(
                "IAM (Nhân viên)", "/api/employee/**",
                "CIAM (Khách hàng)", "/api/customer/**",
                "Public", "/api/public/**"
            )
        );
    }
}
