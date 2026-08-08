package com.marks.usuario.service;

import com.marks.shared.security.UsuarioPrincipal;
import com.marks.usuario.model.PerfilUsuario;
import com.marks.usuario.model.Usuario;
import com.marks.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioDetailsServiceTests {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private Usuario usuario;

    @Test
    void deveCarregarUsuarioPorEmailNormalizado() {
        when(usuarioRepository.findByEmailIgnoreCase("admin@empresa.com"))
                .thenReturn(Optional.of(usuario));
        when(usuario.getId()).thenReturn(1L);
        when(usuario.getNome()).thenReturn("Administrador");
        when(usuario.getEmail()).thenReturn("admin@empresa.com");
        when(usuario.getSenhaHash()).thenReturn("hash-bcrypt");
        when(usuario.getPerfil()).thenReturn(PerfilUsuario.ADMIN);
        when(usuario.isAtivo()).thenReturn(true);
        UsuarioDetailsService service = new UsuarioDetailsService(usuarioRepository);

        UsuarioPrincipal principal = (UsuarioPrincipal) service.loadUserByUsername(" ADMIN@EMPRESA.COM ");

        assertThat(principal.id()).isEqualTo(1L);
        assertThat(principal.isEnabled()).isTrue();
        assertThat(principal.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ADMIN");
        verify(usuarioRepository).findByEmailIgnoreCase("admin@empresa.com");
    }

    @Test
    void deveOcultarSeOEmailNaoExiste() {
        when(usuarioRepository.findByEmailIgnoreCase("ausente@empresa.com"))
                .thenReturn(Optional.empty());
        UsuarioDetailsService service = new UsuarioDetailsService(usuarioRepository);

        assertThatThrownBy(() -> service.loadUserByUsername("ausente@empresa.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Credenciais inválidas");
    }
}
