package com.marks.usuario.exception;

public class EmailJaCadastradoException extends RuntimeException {

    public EmailJaCadastradoException() {
        super("Já existe uma conta para este e-mail");
    }
}
