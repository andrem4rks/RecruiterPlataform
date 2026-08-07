package com.marks.vaga.model;

import com.marks.organizacao.model.Organizacao;
import com.marks.usuario.model.PerfilUsuario;
import com.marks.usuario.model.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "vagas")
public class Vaga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String titulo;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusVaga status = StatusVaga.RASCUNHO;

    @NotNull
    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "responsavel_id", nullable = false)
    private Usuario responsavel;

    @NotNull
    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "organizacao_id", nullable = false, updatable = false)
    private Organizacao organizacao;

    @OneToMany(cascade = ALL, orphanRemoval = true)
    @JoinColumn(name = "vaga_id", nullable = false)
    private List<RequisitoVaga> requisitos = new ArrayList<>();

    @Size(min = 1, message = "A vaga deve possuir ao menos uma competência")
    @OneToMany(cascade = ALL, orphanRemoval = true)
    @JoinColumn(name = "vaga_id", nullable = false)
    private List<VagaCompetencia> competencias = new ArrayList<>();

    @Column(name = "data_limite")
    private LocalDate dataLimite;

    @CreationTimestamp
    @Column(name = "criada_em", nullable = false, updatable = false)
    private Instant criadaEm;

    @UpdateTimestamp
    @Column(name = "atualizada_em", nullable = false)
    private Instant atualizadaEm;

    protected Vaga() {
    }

    public Vaga(
            String titulo,
            String descricao,
            Usuario responsavel,
            LocalDate dataLimite,
            Collection<VagaCompetencia> competencias
    ) {
        this.titulo = titulo;
        this.descricao = descricao;
        definirResponsavelInicial(responsavel);
        this.dataLimite = dataLimite;

        if (competencias == null || competencias.isEmpty()) {
            throw new IllegalArgumentException("A vaga deve possuir ao menos uma competência");
        }
        competencias.forEach(this::adicionarCompetencia);
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public StatusVaga getStatus() {
        return status;
    }

    public void setStatus(StatusVaga status) {
        this.status = status;
    }

    public Usuario getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(Usuario responsavel) {
        validarResponsavel(responsavel);
        if (!mesmaOrganizacao(organizacao, responsavel.getOrganizacao())) {
            throw new IllegalArgumentException("O responsável deve pertencer à organização da vaga");
        }
        this.responsavel = responsavel;
    }

    public Organizacao getOrganizacao() {
        return organizacao;
    }

    private void definirResponsavelInicial(Usuario responsavel) {
        validarResponsavel(responsavel);
        this.responsavel = responsavel;
        this.organizacao = responsavel.getOrganizacao();
    }

    private void validarResponsavel(Usuario responsavel) {
        Objects.requireNonNull(responsavel, "O responsável pela vaga é obrigatório");
        if (responsavel.getPerfil() != PerfilUsuario.ADMIN
                && responsavel.getPerfil() != PerfilUsuario.RESPONSAVEL) {
            throw new IllegalArgumentException("O responsável deve possuir perfil ADMIN ou RESPONSAVEL");
        }
        if (responsavel.getOrganizacao() == null) {
            throw new IllegalArgumentException("O responsável deve pertencer a uma organização");
        }
    }

    private boolean mesmaOrganizacao(Organizacao primeira, Organizacao segunda) {
        if (primeira == segunda) {
            return true;
        }

        Long primeiroId = primeira == null ? null : primeira.getId();
        Long segundoId = segunda == null ? null : segunda.getId();
        return primeiroId != null && primeiroId.equals(segundoId);
    }

    public List<RequisitoVaga> getRequisitos() {
        return Collections.unmodifiableList(requisitos);
    }

    public void adicionarRequisito(RequisitoVaga requisito) {
        requisitos.add(requisito);
    }

    public void removerRequisito(RequisitoVaga requisito) {
        requisitos.remove(requisito);
    }

    public List<VagaCompetencia> getCompetencias() {
        return Collections.unmodifiableList(competencias);
    }

    public void adicionarCompetencia(VagaCompetencia vagaCompetencia) {
        Objects.requireNonNull(vagaCompetencia, "A competência da vaga é obrigatória");
        if (possuiCompetencia(vagaCompetencia)) {
            throw new IllegalArgumentException("A competência já foi adicionada à vaga");
        }
        competencias.add(vagaCompetencia);
    }

    public void removerCompetencia(VagaCompetencia vagaCompetencia) {
        if (competencias.size() == 1 && competencias.contains(vagaCompetencia)) {
            throw new IllegalStateException("A vaga deve manter ao menos uma competência");
        }
        competencias.remove(vagaCompetencia);
    }

    private boolean possuiCompetencia(VagaCompetencia novaCompetencia) {
        return competencias.stream().anyMatch(atual -> {
            if (atual.getCompetencia() == novaCompetencia.getCompetencia()) {
                return true;
            }

            Long idAtual = atual.getCompetencia().getId();
            Long novoId = novaCompetencia.getCompetencia().getId();
            return idAtual != null && idAtual.equals(novoId);
        });
    }

    public LocalDate getDataLimite() {
        return dataLimite;
    }

    public void setDataLimite(LocalDate dataLimite) {
        this.dataLimite = dataLimite;
    }

    public Instant getCriadaEm() {
        return criadaEm;
    }

    public Instant getAtualizadaEm() {
        return atualizadaEm;
    }
}
