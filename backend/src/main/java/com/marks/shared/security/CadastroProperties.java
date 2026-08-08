package com.marks.shared.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Locale;

@ConfigurationProperties(prefix = "app.security.registration")
public record CadastroProperties(String allowedEmailDomain) {

    public CadastroProperties {
        if (allowedEmailDomain == null || allowedEmailDomain.isBlank()) {
            throw new IllegalArgumentException("CORPORATE_EMAIL_DOMAIN é obrigatório");
        }
        allowedEmailDomain = allowedEmailDomain.trim().toLowerCase(Locale.ROOT);
        if (allowedEmailDomain.startsWith("@")) {
            allowedEmailDomain = allowedEmailDomain.substring(1);
        }
    }
}
