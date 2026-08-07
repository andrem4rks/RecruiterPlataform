package com.marks.candidatura.model;

import com.marks.usuario.model.Usuario;
import com.marks.vaga.model.Vaga;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(
        name = "candidaturas",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_candidaturas_vaga_candidato",
                columnNames = {"vaga_id", "candidato_id"}
        )
)
public class Candidatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vaga_id", nullable = false)
    private Vaga vaga;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidato_id", nullable = false)
    private Usuario candidato;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusCandidatura status = StatusCandidatura.ENVIADA;

    @Size(max = 1000)
    @Column(length = 1000)
    private String mensagem;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "candidatura_id", nullable = false)
    private List<HistoricoStatusCandidatura> historicos = new ArrayList<>();

    @OneToOne(
            mappedBy = "candidatura",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Avaliacao avaliacao;

    @CreationTimestamp
    @Column(name = "criada_em", nullable = false, updatable = false)
    private Instant criadaEm;

    @UpdateTimestamp
    @Column(name = "atualizada_em", nullable = false)
    private Instant atualizadaEm;

    protected Candidatura() {
    }

    public Candidatura(Vaga vaga, Usuario candidato, String mensagem) {
        this.vaga = vaga;
        this.candidato = candidato;
        this.mensagem = mensagem;
    }

    public Long getId() {
        return id;
    }

    public Vaga getVaga() {
        return vaga;
    }

    public Usuario getCandidato() {
        return candidato;
    }

    public StatusCandidatura getStatus() {
        return status;
    }

    public void setStatus(StatusCandidatura status) {
        this.status = status;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public List<HistoricoStatusCandidatura> getHistoricos() {
        return Collections.unmodifiableList(historicos);
    }

    public void adicionarHistorico(HistoricoStatusCandidatura historico) {
        historicos.add(historico);
    }

    public Avaliacao getAvaliacao() {
        return avaliacao;
    }

    public void definirAvaliacao(Avaliacao avaliacao) {
        if (this.avaliacao != null) {
            this.avaliacao.associarCandidatura(null);
        }

        this.avaliacao = avaliacao;

        if (avaliacao != null && avaliacao.getCandidatura() != this) {
            avaliacao.associarCandidatura(this);
        }
    }

    public Instant getCriadaEm() {
        return criadaEm;
    }

    public Instant getAtualizadaEm() {
        return atualizadaEm;
    }
}
