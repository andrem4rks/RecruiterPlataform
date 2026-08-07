package com.marks.candidatura.model;

import com.marks.usuario.model.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(
        name = "avaliacoes",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_avaliacoes_candidatura",
                columnNames = "candidatura_id"
        )
)
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidatura_id", nullable = false, unique = true)
    private Candidatura candidatura;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "avaliador_id", nullable = false)
    private Usuario avaliador;

    @NotNull
    @Min(1)
    @Max(5)
    @Column(nullable = false, columnDefinition = "SMALLINT")
    private Short nota;

    @NotBlank
    @Size(max = 2000)
    @Column(nullable = false, length = 2000)
    private String parecer;

    @CreationTimestamp
    @Column(name = "criada_em", nullable = false, updatable = false)
    private Instant criadaEm;

    @UpdateTimestamp
    @Column(name = "atualizada_em", nullable = false)
    private Instant atualizadaEm;

    protected Avaliacao() {
    }

    public Avaliacao(Candidatura candidatura, Usuario avaliador, Short nota, String parecer) {
        this.candidatura = candidatura;
        this.avaliador = avaliador;
        this.nota = nota;
        this.parecer = parecer;
    }

    public Long getId() {
        return id;
    }

    public Candidatura getCandidatura() {
        return candidatura;
    }

    void associarCandidatura(Candidatura candidatura) {
        this.candidatura = candidatura;
    }

    public Usuario getAvaliador() {
        return avaliador;
    }

    public Short getNota() {
        return nota;
    }

    public void setNota(Short nota) {
        this.nota = nota;
    }

    public String getParecer() {
        return parecer;
    }

    public void setParecer(String parecer) {
        this.parecer = parecer;
    }

    public Instant getCriadaEm() {
        return criadaEm;
    }

    public Instant getAtualizadaEm() {
        return atualizadaEm;
    }
}
