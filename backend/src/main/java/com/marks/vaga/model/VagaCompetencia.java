package com.marks.vaga.model;

import com.marks.competencia.model.Competencia;
import com.marks.competencia.model.NivelCompetencia;
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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.Objects;

@Entity
@Table(
        name = "vagas_competencias",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_vagas_competencias_vaga_competencia",
                columnNames = {"vaga_id", "competencia_id"}
        )
)
public class VagaCompetencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "competencia_id", nullable = false)
    private Competencia competencia;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_minimo", nullable = false, length = 30)
    private NivelCompetencia nivelMinimo;

    @PositiveOrZero
    @Column(name = "meses_experiencia_minima", nullable = false)
    private int mesesExperienciaMinima;

    protected VagaCompetencia() {
    }

    public VagaCompetencia(
            Competencia competencia,
            NivelCompetencia nivelMinimo,
            int mesesExperienciaMinima
    ) {
        this.competencia = Objects.requireNonNull(competencia, "A competência é obrigatória");
        if (!competencia.isAtiva()) {
            throw new IllegalArgumentException("A competência deve estar ativa");
        }
        this.nivelMinimo = Objects.requireNonNull(nivelMinimo, "O nível mínimo é obrigatório");
        definirMesesExperienciaMinima(mesesExperienciaMinima);
    }

    public Long getId() {
        return id;
    }

    public Competencia getCompetencia() {
        return competencia;
    }

    public NivelCompetencia getNivelMinimo() {
        return nivelMinimo;
    }

    public void setNivelMinimo(NivelCompetencia nivelMinimo) {
        this.nivelMinimo = Objects.requireNonNull(nivelMinimo, "O nível mínimo é obrigatório");
    }

    public int getMesesExperienciaMinima() {
        return mesesExperienciaMinima;
    }

    public void definirMesesExperienciaMinima(int mesesExperienciaMinima) {
        if (mesesExperienciaMinima < 0) {
            throw new IllegalArgumentException("A experiência mínima não pode ser negativa");
        }
        this.mesesExperienciaMinima = mesesExperienciaMinima;
    }
}
