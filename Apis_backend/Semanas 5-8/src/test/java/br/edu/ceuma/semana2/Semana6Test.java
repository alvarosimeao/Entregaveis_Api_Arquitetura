package br.edu.ceuma.semana2;

import br.edu.ceuma.semana2.entidade.Aluno;
import br.edu.ceuma.semana2.repositorio.AlunoRepositorio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:alunos_paginacao;MODE=PostgreSQL",
    "spring.datasource.username=sa", "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class Semana6Test {
    @Autowired MockMvc mvc;
    @Autowired AlunoRepositorio alunos;

    @Test void combinaFiltroOrdenacaoEPaginacao() throws Exception {
        alunos.save(new Aluno(null, "Ana Oliveira", 31, null));
        alunos.save(new Aluno(null, "Ana Costa", 23, null));
        alunos.save(new Aluno(null, "Ana Silva", 19, null));
        alunos.save(new Aluno(null, "Bruno Dias", 24, null));
        mvc.perform(get("/alunos")
                .param("nome", "ana").param("idadeMin", "20").param("idadeMax", "40")
                .param("sort", "idade").param("order", "desc").param("limit", "1").param("page", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data[0].nome").value("Ana Costa"))
            .andExpect(jsonPath("$.meta.totalItems").value(2))
            .andExpect(jsonPath("$.meta.itemsPerPage").value(1))
            .andExpect(jsonPath("$.meta.currentPage").value(2))
            .andExpect(jsonPath("$.meta.totalPages").value(2));
    }

    @Test void rejeitaLimitesInvalidosEOrdemDesconhecida() throws Exception {
        for (String query : new String[] {"limit=101", "page=0", "page=x", "sort=atributo", "idadeMin=40&idadeMax=20"}) {
            mvc.perform(get(java.net.URI.create("/alunos?" + query)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
        }
    }
}

