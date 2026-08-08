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

class CompetenciasDominioTests {

    @Test
    void deveCompararNiveisSemDependerDaOrdemDoEnum() {
        assertThat(NivelCompetencia.AVANCADO.atendeAoMinimo(NivelCompetencia.INTERMEDIARIO)).isTrue();
        assertThat(NivelCompetencia.INTERMEDIARIO.atendeAoMinimo(NivelCompetencia.INTERMEDIARIO)).isTrue();
        assertThat(NivelCompetencia.BASICO.atendeAoMinimo(NivelCompetencia.AVANCADO)).isFalse();
        assertThat(NivelCompetencia.AVANCADO.atendeAoMinimo(null)).isFalse();
    }

    @Test
    void devePermitirCadastroSemCompetenciaMasExigirCompetenciaNaVaga() {
        Competencia java = new Competencia("Java", null);
        UsuarioCompetencia experienciaJava = new UsuarioCompetencia(
                java,
                NivelCompetencia.INTERMEDIARIO,
                24
        );

        assertThat(novoUsuario(List.of()).getCompetencias()).isEmpty();

        Usuario responsavel = novoUsuario(List.of(experienciaJava));
        assertThatThrownBy(() -> new Vaga(
                "Desenvolvedor Java",
                "Descrição da vaga",
                responsavel,
                LocalDate.now().plusMonths(1),
                List.of()
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ao menos uma competência");
    }

    @Test
    void deveImpedirCompetenciaDuplicadaENumerosNegativos() {
        Competencia java = new Competencia("Java", null);
        UsuarioCompetencia experienciaJava = new UsuarioCompetencia(java, NivelCompetencia.BASICO, 6);
        Usuario usuario = novoUsuario(List.of(experienciaJava));
        VagaCompetencia requisitoJava = new VagaCompetencia(java, NivelCompetencia.INTERMEDIARIO, 12);
        Vaga vaga = new Vaga(
                "Desenvolvedor Java",
                "Descrição da vaga",
                usuario,
                null,
                List.of(requisitoJava)
        );

        assertThatThrownBy(() -> usuario.adicionarCompetencia(
                new UsuarioCompetencia(java, NivelCompetencia.AVANCADO, 36)
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("já foi adicionada");
        assertThatThrownBy(() -> vaga.adicionarCompetencia(
                new VagaCompetencia(java, NivelCompetencia.BASICO, 0)
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("já foi adicionada");
        assertThatThrownBy(() -> new UsuarioCompetencia(java, NivelCompetencia.BASICO, -1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new VagaCompetencia(java, NivelCompetencia.BASICO, -1))
                .isInstanceOf(IllegalArgumentException.class);

        Competencia inativa = new Competencia("Competência inativa", null);
        inativa.desativar();
        assertThatThrownBy(() -> new UsuarioCompetencia(inativa, NivelCompetencia.BASICO, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ativa");
        assertThatThrownBy(() -> new VagaCompetencia(inativa, NivelCompetencia.BASICO, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ativa");
    }

    private Usuario novoUsuario(List<UsuarioCompetencia> competencias) {
        return new Usuario(
                "Usuário",
                "usuario@empresa.com",
                "senha-hash",
                PerfilUsuario.RESPONSAVEL,
                LocalDate.of(2020, 1, 1),
                competencias
        );
    }
}
