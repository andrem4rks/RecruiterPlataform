package com.marks.usuario.exception;

public class UsuarioNaoEncontradoException extends RuntimeException {

    public UsuarioNaoEncontradoException() {
        super("Usuário autenticado não encontrado");
    }
}
