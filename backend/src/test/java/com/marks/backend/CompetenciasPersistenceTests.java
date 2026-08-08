package com.marks.backend;

import com.marks.competencia.model.Competencia;
import com.marks.competencia.model.NivelCompetencia;
import com.marks.usuario.model.PerfilUsuario;
import com.marks.usuario.model.Usuario;
import com.marks.usuario.model.UsuarioCompetencia;
import com.marks.vaga.model.Vaga;
import com.marks.vaga.model.VagaCompetencia;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CompetenciasPersistenceTests {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void devePersistirCompetenciasDeUsuarioEVaga() {
        Competencia java = new Competencia("Java", "Desenvolvimento na plataforma Java");
        Competencia postgresql = new Competencia("PostgreSQL", "Banco de dados relacional");
        entityManager.persist(java);
        entityManager.persist(postgresql);

        Usuario responsavel = new Usuario(
                "Responsável",
                "responsavel.competencias@empresa.com",
                "senha-hash",
                PerfilUsuario.RESPONSAVEL,
                LocalDate.of(2018, 1, 10),
                List.of(new UsuarioCompetencia(java, NivelCompetencia.AVANCADO, 72))
        );
        Usuario candidato = new Usuario(
                "Candidato",
                "candidato.competencias@empresa.com",
                "senha-hash",
                PerfilUsuario.CANDIDATO,
                LocalDate.of(2021, 3, 15),
                List.of(
                        new UsuarioCompetencia(java, NivelCompetencia.INTERMEDIARIO, 36),
                        new UsuarioCompetencia(postgresql, NivelCompetencia.BASICO, 12)
                )
        );
        entityManager.persist(responsavel);
        entityManager.persist(candidato);

        Vaga vaga = new Vaga(
                "Desenvolvedor Java",
                "Desenvolvimento de aplicações internas",
                responsavel,
                LocalDate.now().plusMonths(1),
                List.of(
                        new VagaCompetencia(java, NivelCompetencia.INTERMEDIARIO, 24),
                        new VagaCompetencia(postgresql, NivelCompetencia.BASICO, 6)
                )
        );
        entityManager.persist(vaga);
        entityManager.flush();

        Long candidatoId = candidato.getId();
        Long vagaId = vaga.getId();
        entityManager.clear();

        Usuario candidatoSalvo = entityManager.find(Usuario.class, candidatoId);
        Vaga vagaSalva = entityManager.find(Vaga.class, vagaId);

        assertThat(candidatoSalvo.getCompetencias())
                .hasSize(2)
                .extracting(competencia -> competencia.getCompetencia().getNome())
                .containsExactlyInAnyOrder("Java", "PostgreSQL");
        assertThat(candidatoSalvo.getCompetencias())
                .filteredOn(competencia -> competencia.getCompetencia().getNome().equals("Java"))
                .singleElement()
                .satisfies(competencia -> {
                    assertThat(competencia.getNivel()).isEqualTo(NivelCompetencia.INTERMEDIARIO);
                    assertThat(competencia.getMesesExperiencia()).isEqualTo(36);
                });
        assertThat(vagaSalva.getCompetencias())
                .hasSize(2)
                .filteredOn(competencia -> competencia.getCompetencia().getNome().equals("PostgreSQL"))
                .singleElement()
                .satisfies(competencia -> {
                    assertThat(competencia.getNivelMinimo()).isEqualTo(NivelCompetencia.BASICO);
                    assertThat(competencia.getMesesExperienciaMinima()).isEqualTo(6);
                });
        assertThat(vagaSalva.getResponsavel().getId()).isEqualTo(responsavel.getId());
    }

    @Test
    void devePersistirUsuarioRecemCadastradoSemCompetencias() {
        Usuario candidato = new Usuario(
                "Novo candidato",
                "novo.candidato@empresa.com",
                "senha-hash",
                PerfilUsuario.CANDIDATO,
                LocalDate.of(2024, 1, 8),
                List.of()
        );
        entityManager.persist(candidato);
        entityManager.flush();
        entityManager.clear();

        Usuario salvo = entityManager.find(Usuario.class, candidato.getId());
        assertThat(salvo.getCompetencias()).isEmpty();
    }
}
