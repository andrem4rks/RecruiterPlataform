package com.marks.usuario.service;

import com.marks.shared.security.JwtTokenService;
import com.marks.shared.security.TokenGerado;
import com.marks.shared.security.UsuarioPrincipal;
import com.marks.usuario.dto.LoginRequest;
import com.marks.usuario.dto.LoginResponse;
import com.marks.usuario.exception.CredenciaisInvalidasException;
import com.marks.usuario.mapper.UsuarioMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AutenticacaoService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final UsuarioMapper usuarioMapper;

    public AutenticacaoService(
            AuthenticationManager authenticationManager,
            JwtTokenService jwtTokenService,
            UsuarioMapper usuarioMapper
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.usuarioMapper = usuarioMapper;
    }

    public LoginResponse autenticar(LoginRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        try {
            Authentication authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(email, request.senha())
            );
            UsuarioPrincipal principal = (UsuarioPrincipal) authentication.getPrincipal();
            TokenGerado token = jwtTokenService.gerar(principal);
            return new LoginResponse(
                    token.valor(),
                    "Bearer",
                    token.expiraEm(),
                    usuarioMapper.paraResponse(principal)
            );
        } catch (AuthenticationException exception) {
            throw new CredenciaisInvalidasException();
        }
    }
}
