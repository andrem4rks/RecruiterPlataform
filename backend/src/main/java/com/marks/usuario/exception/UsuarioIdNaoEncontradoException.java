package com.marks.usuario.exception;

public class UsuarioIdNaoEncontradoException extends RuntimeException {

    public UsuarioIdNaoEncontradoException(Long id) {
        super("Usuário " + id + " não encontrado");
    }
}
