package br.edu.ceuma.semana2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:autenticacao;MODE=PostgreSQL",
    "spring.datasource.username=sa", "spring.jpa.hibernate.ddl-auto=create-drop",
    "api.jwt.secret-base64=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="
})
@AutoConfigureMockMvc
class Semana7Test {
    private static final String EMAIL = "demo@example.test";
    private static final String SENHA = "Uma-senha-ficticia-789";
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    @Autowired ObjectMapper json;

    @Test void loginDaAcessoAoPerfilMasNaoAElevacaoDePrivilegios() throws Exception {
        String cadastro = json.writeValueAsString(java.util.Map.of(
            "nome", "Demo", "email", EMAIL, "senha", SENHA, "perfil", "ADMIN"));
        String usuario = mvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON).content(cadastro))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.perfil").value("USER"))
            .andExpect(jsonPath("$.senha").doesNotExist())
            .andReturn().getResponse().getContentAsString();
        long id = json.readTree(usuario).path("id").asLong();
        assertThat(jdbc.queryForObject("select senha_hash from app_users where id=?", String.class, id))
            .startsWith("$2").isNotEqualTo(SENHA);
        mvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON).content(cadastro))
            .andExpect(status().isConflict());
        mvc.perform(get("/perfil")).andExpect(status().isUnauthorized());
        String token = login();
        mvc.perform(get("/perfil").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk()).andExpect(jsonPath("$.email").value(EMAIL));
        mvc.perform(delete("/usuarios/{id}", id).header("Authorization", "Bearer " + token))
            .andExpect(status().isForbidden()).andExpect(jsonPath("$.error").value("FORBIDDEN"));
        jdbc.update("update app_users set perfil = 'ADMIN' where id = ?", id);
        mvc.perform(delete("/usuarios/{id}", id).header("Authorization", "Bearer " + login()))
            .andExpect(status().isNoContent());
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk())
            .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme").value("bearer"));
    }

    private String login() throws Exception {
        String login = json.writeValueAsString(java.util.Map.of("email", EMAIL, "senha", SENHA));
        String resposta = mvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(login))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return json.readTree(resposta).path("token").asText();
    }
}
