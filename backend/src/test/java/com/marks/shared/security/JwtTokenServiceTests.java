package com.marks.shared.security;

import com.marks.usuario.model.PerfilUsuario;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenServiceTests {

    @Test
    void deveGerarJwtAssinadoComIdentidadePerfilEExpiracao() {
        String segredo = "segredo-de-teste-com-mais-de-trinta-e-dois-bytes";
        SecretKey chave = new SecretKeySpec(segredo.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Instant agora = Instant.parse("2026-08-08T00:00:00Z");
        JwtProperties properties = new JwtProperties(segredo, Duration.ofMinutes(15), "test-api");
        JwtTokenService service = new JwtTokenService(
                new NimbusJwtEncoder(new ImmutableSecret<>(chave)),
                properties,
                Clock.fixed(agora, ZoneOffset.UTC)
        );
        UsuarioPrincipal principal = new UsuarioPrincipal(
                42L,
                "Responsável",
                "responsavel@empresa.com",
                "hash",
                PerfilUsuario.RESPONSAVEL,
                true
        );

        TokenGerado token = service.gerar(principal);

        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(chave)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        JwtTimestampValidator timestampValidator = new JwtTimestampValidator();
        timestampValidator.setClock(Clock.fixed(agora, ZoneOffset.UTC));
        decoder.setJwtValidator(timestampValidator);
        Jwt jwt = decoder.decode(token.valor());
        assertThat(jwt.getSubject()).isEqualTo("responsavel@empresa.com");
        assertThat(jwt.getClaimAsString("perfil")).isEqualTo("RESPONSAVEL");
        assertThat(jwt.getClaimAsString("nome")).isEqualTo("Responsável");
        assertThat(jwt.<Number>getClaim("usuario_id").longValue()).isEqualTo(42L);
        assertThat(jwt.getIssuedAt()).isEqualTo(agora);
        assertThat(jwt.getExpiresAt()).isEqualTo(agora.plusSeconds(900));
        assertThat(token.expiraEm()).isEqualTo(agora.plusSeconds(900));
    }
}
