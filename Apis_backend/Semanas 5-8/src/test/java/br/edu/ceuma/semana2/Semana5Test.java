package br.edu.ceuma.semana2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:alunos;MODE=PostgreSQL",
    "spring.jpa.hibernate.ddl-auto=create-drop", "aluno.nome=Exemplo", "aluno.ra=000000",
    "api.jwt.secret-base64=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="
})
@AutoConfigureMockMvc(addFilters = false)
class Semana5Test {
    @Autowired MockMvc mvc;

    @Test void rejeitaNomeAusenteEIdadeInvalida() throws Exception {
        mvc.perform(post("/alunos").contentType(MediaType.APPLICATION_JSON)
            .content("{\"nome\":\"   \",\"idade\":0}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }
}

