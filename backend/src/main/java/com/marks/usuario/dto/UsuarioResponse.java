package com.marks.usuario.dto;

import com.marks.usuario.model.PerfilUsuario;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        PerfilUsuario perfil
) {
}
