package com.marks.competencia.model;

public enum NivelCompetencia {
    BASICO(1),
    INTERMEDIARIO(2),
    AVANCADO(3);

    private final int ordem;

    NivelCompetencia(int ordem) {
        this.ordem = ordem;
    }

    public boolean atendeAoMinimo(NivelCompetencia nivelMinimo) {
        return nivelMinimo != null && ordem >= nivelMinimo.ordem;
    }
}
