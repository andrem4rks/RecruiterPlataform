package com.marks.usuario.service;

import com.marks.shared.security.CadastroProperties;
import com.marks.shared.security.JwtTokenService;
import com.marks.shared.security.TokenGerado;
import com.marks.usuario.dto.CadastroRequest;
import com.marks.usuario.dto.LoginResponse;
import com.marks.usuario.exception.EmailCorporativoInvalidoException;
import com.marks.usuario.exception.EmailJaCadastradoException;
import com.marks.usuario.mapper.UsuarioMapper;
import com.marks.usuario.model.PerfilUsuario;
import com.marks.usuario.model.Usuario;
import com.marks.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CadastroServiceTests {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenService jwtTokenService;

    private CadastroService cadastroService;

    @BeforeEach
    void setUp() {
        cadastroService = new CadastroService(
                usuarioRepository,
                passwordEncoder,
                jwtTokenService,
                new UsuarioMapper(),
                new CadastroProperties("empresa.com")
        );
    }

    @Test
    void deveCriarCandidatoCorporativoComSenhaCodificadaERetornarSessao() {
        when(usuarioRepository.existsByEmailIgnoreCase("nova@empresa.com")).thenReturn(false);
        when(passwordEncoder.encode("Senha@123")).thenReturn("hash-bcrypt");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtTokenService.gerar(any())).thenReturn(
                new TokenGerado("jwt-cadastro", Instant.parse("2026-08-08T00:15:00Z"))
        );

        LoginResponse response = cadastroService.cadastrar(new CadastroRequest(
                "  Nova Colaboradora  ",
                " NOVA@EMPRESA.COM ",
                "Senha@123",
                LocalDate.of(2024, 1, 8)
        ));

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario salvo = captor.getValue();
        assertThat(salvo.getNome()).isEqualTo("Nova Colaboradora");
        assertThat(salvo.getEmail()).isEqualTo("nova@empresa.com");
        assertThat(salvo.getSenhaHash()).isEqualTo("hash-bcrypt");
        assertThat(salvo.getPerfil()).isEqualTo(PerfilUsuario.CANDIDATO);
        assertThat(salvo.getCompetencias()).isEmpty();
        assertThat(response.token()).isEqualTo("jwt-cadastro");
        assertThat(response.usuario().perfil()).isEqualTo(PerfilUsuario.CANDIDATO);
    }

    @Test
    void deveRecusarEmailForaDoDominioCorporativo() {
        assertThatThrownBy(() -> cadastroService.cadastrar(new CadastroRequest(
                "Pessoa Externa",
                "pessoa@gmail.com",
                "Senha@123",
                LocalDate.of(2024, 1, 8)
        )))
                .isInstanceOf(EmailCorporativoInvalidoException.class)
                .hasMessageContaining("@empresa.com");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveRecusarEmailCorporativoDuplicado() {
        when(usuarioRepository.existsByEmailIgnoreCase("existente@empresa.com")).thenReturn(true);

        assertThatThrownBy(() -> cadastroService.cadastrar(new CadastroRequest(
                "Pessoa Existente",
                "existente@empresa.com",
                "Senha@123",
                LocalDate.of(2024, 1, 8)
        )))
                .isInstanceOf(EmailJaCadastradoException.class);
    }
}
