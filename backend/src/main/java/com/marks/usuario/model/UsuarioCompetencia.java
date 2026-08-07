package com.marks.usuario.model;

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
        name = "usuarios_competencias",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_usuarios_competencias_usuario_competencia",
                columnNames = {"usuario_id", "competencia_id"}
        )
)
public class UsuarioCompetencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "competencia_id", nullable = false)
    private Competencia competencia;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NivelCompetencia nivel;

    @PositiveOrZero
    @Column(name = "meses_experiencia", nullable = false)
    private int mesesExperiencia;

    protected UsuarioCompetencia() {
    }

    public UsuarioCompetencia(
            Competencia competencia,
            NivelCompetencia nivel,
            int mesesExperiencia
    ) {
        this.competencia = Objects.requireNonNull(competencia, "A competência é obrigatória");
        if (!competencia.isAtiva()) {
            throw new IllegalArgumentException("A competência deve estar ativa");
        }
        this.nivel = Objects.requireNonNull(nivel, "O nível da competência é obrigatório");
        definirMesesExperiencia(mesesExperiencia);
    }

    public Long getId() {
        return id;
    }

    public Competencia getCompetencia() {
        return competencia;
    }

    public NivelCompetencia getNivel() {
        return nivel;
    }

    public void setNivel(NivelCompetencia nivel) {
        this.nivel = Objects.requireNonNull(nivel, "O nível da competência é obrigatório");
    }

    public int getMesesExperiencia() {
        return mesesExperiencia;
    }

    public void definirMesesExperiencia(int mesesExperiencia) {
        if (mesesExperiencia < 0) {
            throw new IllegalArgumentException("Os meses de experiência não podem ser negativos");
        }
        this.mesesExperiencia = mesesExperiencia;
    }
}
