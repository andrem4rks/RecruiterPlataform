package com.marks.usuario.mapper;

import com.marks.shared.security.UsuarioPrincipal;
import com.marks.usuario.dto.UsuarioResponse;
import com.marks.usuario.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioResponse paraResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil()
        );
    }

    public UsuarioResponse paraResponse(UsuarioPrincipal principal) {
        return new UsuarioResponse(
                principal.id(),
                principal.nome(),
                principal.getUsername(),
                principal.perfil()
        );
    }
}
