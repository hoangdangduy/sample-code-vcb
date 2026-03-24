package com.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller dành cho khách hàng - được bảo vệ bởi CIAM (Auth0).
 */
@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @GetMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public Map<String, Object> getProfile(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
            "message", "✅ Chào khách hàng! Đây là hồ sơ của bạn.",
            "source", "CIAM (Auth0)",
            "customerId", jwt.getSubject(),
            "name", getClaimSafe(jwt, "name"),
            "email", getClaimSafe(jwt, "email"),
            "emailVerified", getClaimSafe(jwt, "email_verified"),
            "picture", getClaimSafe(jwt, "picture"),
            "issuer", jwt.getIssuer().toString()
        );
    }

    @GetMapping("/orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    public Map<String, Object> getOrders(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
            "message", "📦 Đơn hàng của bạn",
            "customerId", jwt.getSubject(),
            "orders", java.util.List.of(
                Map.of("orderId", "ORD-001", "status", "Đang giao", "total", "500.000đ"),
                Map.of("orderId", "ORD-002", "status", "Đã giao",   "total", "250.000đ")
            )
        );
    }

    @GetMapping("/products")
    public Map<String, Object> getPublicProducts() {
        return Map.of(
            "message", "🛒 Danh sách sản phẩm (public)",
            "products", java.util.List.of(
                Map.of("id", 1, "name", "Sản phẩm A", "price", "100.000đ"),
                Map.of("id", 2, "name", "Sản phẩm B", "price", "200.000đ")
            )
        );
    }

    private String getClaimSafe(Jwt jwt, String claim) {
        Object value = jwt.getClaim(claim);
        return value != null ? value.toString() : "N/A";
    }
}
