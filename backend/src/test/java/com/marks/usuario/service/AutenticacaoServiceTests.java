package com.marks.usuario.service;

import com.marks.shared.security.JwtTokenService;
import com.marks.shared.security.TokenGerado;
import com.marks.shared.security.UsuarioPrincipal;
import com.marks.usuario.dto.LoginRequest;
import com.marks.usuario.dto.LoginResponse;
import com.marks.usuario.exception.CredenciaisInvalidasException;
import com.marks.usuario.mapper.UsuarioMapper;
import com.marks.usuario.model.PerfilUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutenticacaoServiceTests {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private Authentication authentication;

    private AutenticacaoService autenticacaoService;

    @BeforeEach
    void setUp() {
        autenticacaoService = new AutenticacaoService(
                authenticationManager,
                jwtTokenService,
                new UsuarioMapper()
        );
    }

    @Test
    void deveAutenticarCredenciaisValidasERetornarTokenEUsuario() {
        UsuarioPrincipal principal = new UsuarioPrincipal(
                7L,
                "Candidato Demo",
                "candidato@empresa.com",
                "hash",
                PerfilUsuario.CANDIDATO,
                true
        );
        Instant expiracao = Instant.parse("2026-08-08T00:15:00Z");
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(principal);
        when(jwtTokenService.gerar(principal)).thenReturn(new TokenGerado("jwt-assinado", expiracao));

        LoginResponse response = autenticacaoService.autenticar(
                new LoginRequest("  CANDIDATO@EMPRESA.COM ", "Cand@123")
        );

        assertThat(response.token()).isEqualTo("jwt-assinado");
        assertThat(response.tipo()).isEqualTo("Bearer");
        assertThat(response.expiraEm()).isEqualTo(expiracao);
        assertThat(response.usuario().id()).isEqualTo(7L);
        assertThat(response.usuario().perfil()).isEqualTo(PerfilUsuario.CANDIDATO);

        ArgumentCaptor<Authentication> captor = ArgumentCaptor.forClass(Authentication.class);
        verify(authenticationManager).authenticate(captor.capture());
        UsernamePasswordAuthenticationToken credenciais =
                (UsernamePasswordAuthenticationToken) captor.getValue();
        assertThat(credenciais.getPrincipal()).isEqualTo("candidato@empresa.com");
        assertThat(credenciais.getCredentials()).isEqualTo("Cand@123");
    }

    @Test
    void deveRecusarCredenciaisInvalidasSemExporDetalhes() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("senha incorreta"));

        assertThatThrownBy(() -> autenticacaoService.autenticar(
                new LoginRequest("candidato@empresa.com", "incorreta")
        ))
                .isInstanceOf(CredenciaisInvalidasException.class)
                .hasMessage("E-mail ou senha inválidos");
    }
}
