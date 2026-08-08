package com.marks.backend;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AutenticacaoIntegracaoTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveAutenticarUsuarioDemoEConsultarProprioPerfilComJwtReal() throws Exception {
        String resposta = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"candidato@empresa.com","senha":"Cand@123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario.perfil").value("CANDIDATO"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        String token = JsonPath.read(resposta, "$.token");

        mockMvc.perform(get("/api/v1/usuarios/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("candidato@empresa.com"));
    }

    @Test
    void deveCadastrarColaboradorCorporativoComPerfilCandidato() throws Exception {
        mockMvc.perform(post("/api/v1/auth/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome":"Colaboradora Integração",
                                  "email":"integracao@empresa.com",
                                  "senha":"Senha@123",
                                  "dataAdmissao":"2024-01-08"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.usuario.perfil").value("CANDIDATO"));
    }
}
