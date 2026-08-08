package com.marks.backend;

import com.marks.competencia.model.Competencia;
import com.marks.competencia.model.NivelCompetencia;
import com.marks.usuario.model.PerfilUsuario;
import com.marks.usuario.model.Usuario;
import com.marks.usuario.model.UsuarioCompetencia;
import com.marks.vaga.model.Vaga;
import com.marks.vaga.model.VagaCompetencia;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VagasDominioTests {

    @Test
    void devePermitirTrocarResponsavelEntreColaboradoresAutorizados() {
        Competencia java = new Competencia("Java", null);
        Usuario responsavelA = novoUsuario("Responsável A", PerfilUsuario.RESPONSAVEL, java);
        Usuario responsavelB = novoUsuario("Responsável B", PerfilUsuario.RESPONSAVEL, java);
        Vaga vaga = novaVaga(responsavelA, java);

        vaga.setResponsavel(responsavelB);

        assertThat(vaga.getResponsavel()).isSameAs(responsavelB);
    }

    @Test
    void deveRecusarCandidatoComoResponsavel() {
        Competencia java = new Competencia("Java", null);
        Usuario candidato = novoUsuario("Candidato", PerfilUsuario.CANDIDATO, java);

        assertThatThrownBy(() -> novaVaga(candidato, java))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ADMIN ou RESPONSAVEL");
    }

    private Usuario novoUsuario(String nome, PerfilUsuario perfil, Competencia competencia) {
        return new Usuario(
                nome,
                nome.toLowerCase().replace(" ", ".") + "@empresa.com",
                "senha-hash",
                perfil,
                LocalDate.of(2020, 1, 1),
                List.of(new UsuarioCompetencia(competencia, NivelCompetencia.AVANCADO, 48))
        );
    }

    private Vaga novaVaga(Usuario responsavel, Competencia competencia) {
        return new Vaga(
                "Desenvolvedor Java",
                "Descrição da vaga",
                responsavel,
                LocalDate.now().plusMonths(1),
                List.of(new VagaCompetencia(competencia, NivelCompetencia.INTERMEDIARIO, 24))
        );
    }
}
