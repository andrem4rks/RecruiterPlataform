package com.marks.backend;

import com.marks.competencia.model.Competencia;
import com.marks.competencia.model.NivelCompetencia;
import com.marks.organizacao.model.Organizacao;
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

class OrganizacoesDominioTests {

    @Test
    void deveDerivarAOrganizacaoDaVagaAPartirDoResponsavel() {
        Organizacao empresa = new Organizacao("Empresa A");
        Competencia java = new Competencia("Java", null);
        Usuario responsavel = novoUsuario("Responsável", PerfilUsuario.RESPONSAVEL, empresa, java);

        Vaga vaga = novaVaga(responsavel, java);

        assertThat(vaga.getOrganizacao()).isSameAs(empresa);
    }

    @Test
    void deveRecusarResponsavelDeOutraOrganizacao() {
        Organizacao empresaA = new Organizacao("Empresa A");
        Organizacao empresaB = new Organizacao("Empresa B");
        Competencia java = new Competencia("Java", null);
        Usuario responsavelA = novoUsuario("Responsável A", PerfilUsuario.RESPONSAVEL, empresaA, java);
        Usuario responsavelB = novoUsuario("Responsável B", PerfilUsuario.RESPONSAVEL, empresaB, java);
        Vaga vaga = novaVaga(responsavelA, java);

        assertThatThrownBy(() -> vaga.setResponsavel(responsavelB))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("organização da vaga");
        assertThat(vaga.getResponsavel()).isSameAs(responsavelA);
    }

    @Test
    void deveRecusarCandidatoComoResponsavelEOrganizacaoInativa() {
        Organizacao empresa = new Organizacao("Empresa A");
        Competencia java = new Competencia("Java", null);
        Usuario candidato = novoUsuario("Candidato", PerfilUsuario.CANDIDATO, empresa, java);

        assertThatThrownBy(() -> novaVaga(candidato, java))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ADMIN ou RESPONSAVEL");

        Organizacao inativa = new Organizacao("Empresa inativa");
        inativa.desativar();
        assertThatThrownBy(() -> novoUsuario("Responsável", PerfilUsuario.RESPONSAVEL, inativa, java))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ativa");
    }

    private Usuario novoUsuario(
            String nome,
            PerfilUsuario perfil,
            Organizacao organizacao,
            Competencia competencia
    ) {
        return new Usuario(
                nome,
                nome.toLowerCase().replace(" ", ".") + "@empresa.com",
                "senha-hash",
                perfil,
                LocalDate.of(2020, 1, 1),
                organizacao,
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
