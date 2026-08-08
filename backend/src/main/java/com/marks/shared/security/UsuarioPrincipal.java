package com.marks.shared.security;

import com.marks.usuario.model.PerfilUsuario;
import com.marks.usuario.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record UsuarioPrincipal(
        Long id,
        String nome,
        String email,
        String senhaHash,
        PerfilUsuario perfil,
        boolean ativo
) implements UserDetails {

    public static UsuarioPrincipal de(Usuario usuario) {
        return new UsuarioPrincipal(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getSenhaHash(),
                usuario.getPerfil(),
                usuario.isAtivo()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + perfil.name()));
    }

    @Override
    public String getPassword() {
        return senhaHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return ativo;
    }
}
