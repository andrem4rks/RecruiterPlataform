package com.marks.usuario.controller;

import com.marks.usuario.dto.CadastroRequest;
import com.marks.usuario.dto.LoginRequest;
import com.marks.usuario.dto.LoginResponse;
import com.marks.usuario.service.AutenticacaoService;
import com.marks.usuario.service.CadastroService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AutenticacaoService autenticacaoService;
    private final CadastroService cadastroService;

    public AuthController(AutenticacaoService autenticacaoService, CadastroService cadastroService) {
        this.autenticacaoService = autenticacaoService;
        this.cadastroService = cadastroService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(autenticacaoService.autenticar(request));
    }

    @PostMapping("/cadastro")
    public ResponseEntity<LoginResponse> cadastrar(@Valid @RequestBody CadastroRequest request) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
                .body(cadastroService.cadastrar(request));
    }
}
