package com.marks.candidatura.model;

import com.marks.usuario.model.Usuario;
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
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "historico_status_candidatura")
public class HistoricoStatusCandidatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_anterior", length = 30)
    private StatusCandidatura statusAnterior;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status_novo", nullable = false, length = 30)
    private StatusCandidatura statusNovo;

    @Size(max = 1000)
    @Column(length = 1000)
    private String comentario;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alterado_por_id", nullable = false)
    private Usuario alteradoPor;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    protected HistoricoStatusCandidatura() {
    }

    public HistoricoStatusCandidatura(
            StatusCandidatura statusAnterior,
            StatusCandidatura statusNovo,
            Usuario alteradoPor,
            String comentario
    ) {
        this.statusAnterior = statusAnterior;
        this.statusNovo = statusNovo;
        this.alteradoPor = alteradoPor;
        this.comentario = comentario;
    }

    public Long getId() {
        return id;
    }

    public StatusCandidatura getStatusAnterior() {
        return statusAnterior;
    }

    public StatusCandidatura getStatusNovo() {
        return statusNovo;
    }

    public String getComentario() {
        return comentario;
    }

    public Usuario getAlteradoPor() {
        return alteradoPor;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}
