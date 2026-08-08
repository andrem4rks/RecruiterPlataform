package com.marks.usuario.service;

import com.marks.usuario.dto.UsuarioResponse;
import com.marks.usuario.exception.UsuarioNaoEncontradoException;
import com.marks.usuario.exception.UsuarioIdNaoEncontradoException;
import com.marks.usuario.model.PerfilUsuario;
import com.marks.usuario.mapper.UsuarioMapper;
import com.marks.usuario.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarAtual(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email)
                .filter(usuario -> usuario.isAtivo())
                .map(usuarioMapper::paraResponse)
                .orElseThrow(UsuarioNaoEncontradoException::new);
    }

    @Transactional
    public UsuarioResponse alterarPerfil(Long id, PerfilUsuario perfil) {
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioIdNaoEncontradoException(id));
        usuario.setPerfil(perfil);
        return usuarioMapper.paraResponse(usuario);
    }
}
