package com.marks.shared.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.security.jwt")
public record JwtProperties(
        String secret,
        Duration expiration,
        String issuer
) {

    public JwtProperties {
        if (secret == null || secret.getBytes(java.nio.charset.StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException("JWT_SECRET deve possuir ao menos 32 bytes");
        }
        if (expiration == null || expiration.isZero() || expiration.isNegative()) {
            throw new IllegalArgumentException("JWT_EXPIRATION deve ser uma duração positiva");
        }
        if (issuer == null || issuer.isBlank()) {
            throw new IllegalArgumentException("O emissor do JWT é obrigatório");
        }
    }
}
