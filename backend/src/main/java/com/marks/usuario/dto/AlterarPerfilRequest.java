package com.marks.usuario.dto;

import com.marks.usuario.model.PerfilUsuario;
import jakarta.validation.constraints.NotNull;

public record AlterarPerfilRequest(@NotNull PerfilUsuario perfil) {
}
