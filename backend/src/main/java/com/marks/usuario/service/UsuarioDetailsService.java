package com.marks.usuario.service;

import com.marks.shared.security.UsuarioPrincipal;
import com.marks.usuario.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String emailNormalizado = normalizarEmail(email);
        return usuarioRepository.findByEmailIgnoreCase(emailNormalizado)
                .map(UsuarioPrincipal::de)
                .orElseThrow(() -> new UsernameNotFoundException("Credenciais inválidas"));
    }

    private String normalizarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
