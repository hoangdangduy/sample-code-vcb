package com.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Controller dành cho nhân viên nội bộ - được bảo vệ bởi IAM (Keycloak).
 */
@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @GetMapping("/profile")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public Map<String, Object> getProfile(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
            "message", "✅ Chào nhân viên! Đây là dữ liệu nội bộ.",
            "source", "IAM (Keycloak)",
            "employeeId", jwt.getSubject(),
            "name", getClaimSafe(jwt, "name"),
            "email", getClaimSafe(jwt, "email"),
            "department", getClaimSafe(jwt, "department"),
            "roles", getRealmRoles(jwt),
            "issuer", jwt.getIssuer().toString()
        );
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> getDashboard(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
            "message", "🔒 Admin Dashboard - Dữ liệu nhạy cảm nội bộ",
            "source", "IAM (Keycloak)",
            "totalEmployees", 250,
            "activeUsers", 180,
            "systemHealth", "OK"
        );
    }

    @GetMapping("/resources")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public Map<String, Object> getResources() {
        return Map.of(
            "message", "📁 Tài nguyên nội bộ",
            "documents", java.util.List.of("policy.pdf", "handbook.pdf", "it-guide.pdf"),
            "intranetUrl", "http://intranet.company.internal"
        );
    }

    private String getClaimSafe(Jwt jwt, String claim) {
        Object value = jwt.getClaim(claim);
        return value != null ? value.toString() : "N/A";
    }

    @SuppressWarnings("unchecked")
    private List<String> getRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            Object roles = realmAccess.get("roles");
            if (roles instanceof List) {
                return (List<String>) roles;
            }
        }
        return Collections.emptyList();
    }
}
