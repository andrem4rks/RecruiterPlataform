package com.marks.usuario.controller;

import com.marks.usuario.exception.CredenciaisInvalidasException;
import com.marks.usuario.exception.UsuarioNaoEncontradoException;
import com.marks.usuario.exception.EmailCorporativoInvalidoException;
import com.marks.usuario.exception.EmailJaCadastradoException;
import com.marks.usuario.exception.UsuarioIdNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {AuthController.class, UsuarioController.class})
public class UsuarioExceptionHandler {

    @ExceptionHandler({CredenciaisInvalidasException.class, UsuarioNaoEncontradoException.class})
    ProblemDetail tratarNaoAutorizado(RuntimeException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(EmailCorporativoInvalidoException.class)
    ProblemDetail tratarEmailCorporativo(EmailCorporativoInvalidoException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    ProblemDetail tratarEmailDuplicado(EmailJaCadastradoException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(UsuarioIdNaoEncontradoException.class)
    ProblemDetail tratarUsuarioNaoEncontrado(UsuarioIdNaoEncontradoException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }
}
