package com.marks.usuario.exception;

public class EmailCorporativoInvalidoException extends RuntimeException {

    public EmailCorporativoInvalidoException(String dominio) {
        super("Utilize um e-mail corporativo @" + dominio);
    }
}
