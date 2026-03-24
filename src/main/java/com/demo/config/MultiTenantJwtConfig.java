package com.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cấu hình Multi-Tenant JWT Decoder:
 * - IAM  (Keycloak) → nhân viên nội bộ
 * - CIAM (Auth0)    → khách hàng
 *
 * Cách hoạt động: Dựa vào claim "iss" (issuer) trong JWT token
 * để xác định token đến từ hệ thống nào, rồi dùng đúng JWK để verify.
 */
@Configuration
public class MultiTenantJwtConfig {

    private static final Logger log = LoggerFactory.getLogger(MultiTenantJwtConfig.class);

    @Value("${app.security.iam.issuer-uri}")
    private String iamIssuerUri;

    @Value("${app.security.iam.jwk-set-uri}")
    private String iamJwkSetUri;

    private final Map<String, JwtDecoder> decoderCache = new ConcurrentHashMap<>();

    @Bean
    public JwtDecoder jwtDecoder() {
        return token -> {
            String issuer = extractIssuerWithoutVerification(token);
            JwtDecoder decoder = decoderCache.computeIfAbsent(issuer, this::createDecoderForIssuer);
            return decoder.decode(token);
        };
    }

    private JwtDecoder createDecoderForIssuer(String issuer) {
        String jwkSetUri;

        if (issuer.contains(iamIssuerUri) || iamIssuerUri.contains(issuer)) {
            jwkSetUri = iamJwkSetUri;
            log.info("🔐 [IAM] Sử dụng Keycloak decoder cho issuer: {}", issuer);
        } else {
            throw new JwtException("❌ Issuer không được tin tưởng: " + issuer);
        }

        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withJwkSetUri(jwkSetUri)
                .build();

        OAuth2TokenValidator<Jwt> issuerValidator = JwtValidators.createDefaultWithIssuer(issuer);
        decoder.setJwtValidator(issuerValidator);

        return decoder;
    }

    private String extractIssuerWithoutVerification(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                throw new JwtException("JWT không hợp lệ: thiếu phần payload");
            }
            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<?, ?> claims = mapper.readValue(payload, Map.class);
            Object iss = claims.get("iss");
            if (iss == null) {
                throw new JwtException("JWT không có claim 'iss'");
            }
            return iss.toString();
        } catch (Exception e) {
            throw new JwtException("Không thể đọc issuer từ JWT: " + e.getMessage());
        }
    }
}
