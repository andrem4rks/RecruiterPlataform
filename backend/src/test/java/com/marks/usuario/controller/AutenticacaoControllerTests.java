package com.marks.usuario.controller;

import com.marks.shared.config.SecurityConfig;
import com.marks.usuario.dto.LoginResponse;
import com.marks.usuario.dto.UsuarioResponse;
import com.marks.usuario.exception.CredenciaisInvalidasException;
import com.marks.usuario.model.PerfilUsuario;
import com.marks.usuario.service.AutenticacaoService;
import com.marks.usuario.service.CadastroService;
import com.marks.usuario.service.UsuarioDetailsService;
import com.marks.usuario.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AuthController.class, UsuarioController.class})
@Import({SecurityConfig.class, UsuarioExceptionHandler.class})
class AutenticacaoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AutenticacaoService autenticacaoService;

    @MockitoBean
    private CadastroService cadastroService;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void loginValidoDeveRetornarTokenEDadosBasicos() throws Exception {
        UsuarioResponse usuario = new UsuarioResponse(
                3L,
                "Candidato Demo",
                "candidato@empresa.com",
                PerfilUsuario.CANDIDATO
        );
        when(autenticacaoService.autenticar(any())).thenReturn(new LoginResponse(
                "jwt-assinado",
                "Bearer",
                Instant.parse("2026-08-08T00:15:00Z"),
                usuario
        ));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "candidato@empresa.com",
                                  "senha": "Cand@123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-assinado"))
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.usuario.email").value("candidato@empresa.com"))
                .andExpect(jsonPath("$.usuario.perfil").value("CANDIDATO"));
    }

    @Test
    void loginInvalidoDeveRetornar401() throws Exception {
        when(autenticacaoService.autenticar(any())).thenThrow(new CredenciaisInvalidasException());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"candidato@empresa.com","senha":"incorreta"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("E-mail ou senha inválidos"));
    }

    @Test
    void loginMalformadoDeveRetornar400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"email-invalido","senha":""}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cadastroValidoDeveRetornar201ESessao() throws Exception {
        UsuarioResponse usuario = new UsuarioResponse(
                8L,
                "Nova Colaboradora",
                "nova@empresa.com",
                PerfilUsuario.CANDIDATO
        );
        when(cadastroService.cadastrar(any())).thenReturn(new LoginResponse(
                "jwt-cadastro",
                "Bearer",
                Instant.parse("2026-08-08T00:15:00Z"),
                usuario
        ));

        mockMvc.perform(post("/api/v1/auth/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome":"Nova Colaboradora",
                                  "email":"nova@empresa.com",
                                  "senha":"Senha@123",
                                  "dataAdmissao":"2024-01-08"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt-cadastro"))
                .andExpect(jsonPath("$.usuario.perfil").value("CANDIDATO"));
    }

    @Test
    void rotaPrivadaSemTokenDeveRetornar401() throws Exception {
        mockMvc.perform(get("/api/v1/usuarios/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void usuarioAutenticadoDeveConsultarProprioPerfil() throws Exception {
        UsuarioResponse usuario = new UsuarioResponse(
                3L,
                "Candidato Demo",
                "candidato@empresa.com",
                PerfilUsuario.CANDIDATO
        );
        when(usuarioService.buscarAtual("candidato@empresa.com")).thenReturn(usuario);

        mockMvc.perform(get("/api/v1/usuarios/me")
                        .with(jwt().jwt(token -> token.subject("candidato@empresa.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.perfil").value("CANDIDATO"));
    }

    @Test
    void candidatoAutenticadoEmRotaDeGestaoDeveRetornar403() throws Exception {
        mockMvc.perform(post("/api/v1/vagas")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CANDIDATO")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void somenteAdminDeveAlterarPerfil() throws Exception {
        UsuarioResponse responsavel = new UsuarioResponse(
                8L,
                "Nova Colaboradora",
                "nova@empresa.com",
                PerfilUsuario.RESPONSAVEL
        );
        when(usuarioService.alterarPerfil(8L, PerfilUsuario.RESPONSAVEL)).thenReturn(responsavel);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .patch("/api/v1/usuarios/8/perfil")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"perfil\":\"RESPONSAVEL\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.perfil").value("RESPONSAVEL"));
    }
}
