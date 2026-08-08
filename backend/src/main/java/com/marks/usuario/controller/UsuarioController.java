package com.marks.usuario.controller;

import com.marks.usuario.dto.AlterarPerfilRequest;
import com.marks.usuario.dto.UsuarioResponse;
import com.marks.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/me")
    public UsuarioResponse atual(Authentication authentication) {
        return usuarioService.buscarAtual(authentication.getName());
    }

    @PatchMapping("/{id}/perfil")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public UsuarioResponse alterarPerfil(
            @PathVariable Long id,
            @Valid @RequestBody AlterarPerfilRequest request
    ) {
        return usuarioService.alterarPerfil(id, request.perfil());
    }
}
