package br.edu.ceuma.semana2.controlador;

import br.edu.ceuma.semana2.dto.AlunoRequest;
import br.edu.ceuma.semana2.dto.AlunoResponse;
import br.edu.ceuma.semana2.dto.Pagina;
import br.edu.ceuma.semana2.servico.AlunoServico;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/alunos")
public class AlunoControlador {

    private final AlunoServico alunoServico;
    private final String nomeAluno;
    private final String raAluno;

    public AlunoControlador(
            AlunoServico alunoServico,
            @Value("${aluno.nome}") String nomeAluno,
            @Value("${aluno.ra}") String raAluno
    ) {
        this.alunoServico = alunoServico;
        this.nomeAluno = nomeAluno;
        this.raAluno = raAluno;
    }

    @GetMapping
    public Pagina<AlunoResponse> listar(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Integer idadeMin,
            @RequestParam(required = false) Integer idadeMax) {
        return alunoServico.listar(page, limit, sort, order, nome, idadeMin, idadeMax);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlunoResponse> buscarPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(alunoServico.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<AlunoResponse> criar(
            @Valid @RequestBody AlunoRequest request
    ) {
        AlunoResponse resposta = alunoServico.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AlunoRequest request
    ) {
        alunoServico.atualizar(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        alunoServico.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ra")
    public String informarRa() {
        return "O RA do aluno " + nomeAluno + " é " + raAluno;
    }
}
