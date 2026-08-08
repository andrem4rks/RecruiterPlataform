package com.marks.usuario.service;

import com.marks.shared.security.CadastroProperties;
import com.marks.shared.security.JwtTokenService;
import com.marks.shared.security.TokenGerado;
import com.marks.shared.security.UsuarioPrincipal;
import com.marks.usuario.dto.CadastroRequest;
import com.marks.usuario.dto.LoginResponse;
import com.marks.usuario.exception.EmailCorporativoInvalidoException;
import com.marks.usuario.exception.EmailJaCadastradoException;
import com.marks.usuario.mapper.UsuarioMapper;
import com.marks.usuario.model.PerfilUsuario;
import com.marks.usuario.model.Usuario;
import com.marks.usuario.repository.UsuarioRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class CadastroService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final UsuarioMapper usuarioMapper;
    private final CadastroProperties properties;

    public CadastroService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService,
            UsuarioMapper usuarioMapper,
            CadastroProperties properties
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.usuarioMapper = usuarioMapper;
        this.properties = properties;
    }

    @Transactional
    public LoginResponse cadastrar(CadastroRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        validarDominio(email);
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailJaCadastradoException();
        }

        Usuario usuario = new Usuario(
                request.nome().trim(),
                email,
                passwordEncoder.encode(request.senha()),
                PerfilUsuario.CANDIDATO,
                request.dataAdmissao(),
                List.of()
        );

        try {
            Usuario salvo = usuarioRepository.save(usuario);
            UsuarioPrincipal principal = UsuarioPrincipal.de(salvo);
            TokenGerado token = jwtTokenService.gerar(principal);
            return new LoginResponse(
                    token.valor(),
                    "Bearer",
                    token.expiraEm(),
                    usuarioMapper.paraResponse(salvo)
            );
        } catch (DataIntegrityViolationException exception) {
            throw new EmailJaCadastradoException();
        }
    }

    private void validarDominio(String email) {
        if (!email.endsWith("@" + properties.allowedEmailDomain())) {
            throw new EmailCorporativoInvalidoException(properties.allowedEmailDomain());
        }
    }
}
