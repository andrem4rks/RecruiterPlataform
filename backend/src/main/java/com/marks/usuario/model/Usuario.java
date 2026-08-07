package com.marks.usuario.model;

import com.marks.organizacao.model.Organizacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static jakarta.persistence.CascadeType.ALL;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String nome;

    @NotBlank
    @Email
    @Size(max = 180)
    @Column(nullable = false, length = 180)
    private String email;

    @NotBlank
    @Size(max = 255)
    @Column(name = "senha_hash", nullable = false, length = 255)
    private String senhaHash;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PerfilUsuario perfil;

    @NotNull
    @Column(name = "data_admissao", nullable = false)
    private LocalDate dataAdmissao;

    @Column(nullable = false)
    private boolean ativo = true;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizacao_id", nullable = false)
    private Organizacao organizacao;

    @Size(min = 1, message = "O usuário deve possuir ao menos uma competência")
    @OneToMany(cascade = ALL, orphanRemoval = true)
    @JoinColumn(name = "usuario_id", nullable = false)
    private List<UsuarioCompetencia> competencias = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected Usuario() {
    }

    public Usuario(
            String nome,
            String email,
            String senhaHash,
            PerfilUsuario perfil,
            LocalDate dataAdmissao,
            Organizacao organizacao,
            Collection<UsuarioCompetencia> competencias
    ) {
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.perfil = perfil;
        this.dataAdmissao = dataAdmissao;
        this.organizacao = Objects.requireNonNull(organizacao, "A organização do usuário é obrigatória");

        if (!organizacao.isAtiva()) {
            throw new IllegalArgumentException("A organização do usuário deve estar ativa");
        }

        if (competencias == null || competencias.isEmpty()) {
            throw new IllegalArgumentException("O usuário deve possuir ao menos uma competência");
        }
        competencias.forEach(this::adicionarCompetencia);
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }

    public LocalDate getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(LocalDate dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Organizacao getOrganizacao() {
        return organizacao;
    }

    public List<UsuarioCompetencia> getCompetencias() {
        return Collections.unmodifiableList(competencias);
    }

    public void adicionarCompetencia(UsuarioCompetencia usuarioCompetencia) {
        Objects.requireNonNull(usuarioCompetencia, "A competência do usuário é obrigatória");
        if (possuiCompetencia(usuarioCompetencia)) {
            throw new IllegalArgumentException("A competência já foi adicionada ao usuário");
        }
        competencias.add(usuarioCompetencia);
    }

    public void removerCompetencia(UsuarioCompetencia usuarioCompetencia) {
        if (competencias.size() == 1 && competencias.contains(usuarioCompetencia)) {
            throw new IllegalStateException("O usuário deve manter ao menos uma competência");
        }
        competencias.remove(usuarioCompetencia);
    }

    private boolean possuiCompetencia(UsuarioCompetencia novaCompetencia) {
        return competencias.stream().anyMatch(atual -> {
            if (atual.getCompetencia() == novaCompetencia.getCompetencia()) {
                return true;
            }

            Long idAtual = atual.getCompetencia().getId();
            Long novoId = novaCompetencia.getCompetencia().getId();
            return idAtual != null && idAtual.equals(novoId);
        });
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }
}
