package br.edu.ceuma.semana2.controlador;

import br.edu.ceuma.semana2.dto.AlunoRequest;
import br.edu.ceuma.semana2.dto.AlunoResponse;
import br.edu.ceuma.semana2.servico.AlunoServico;
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

import java.util.List;

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
    public ResponseEntity<List<AlunoResponse>> listar() {
        return ResponseEntity.ok(alunoServico.listarTodos());
    }

    @GetMapping(params = "nome")
    public ResponseEntity<List<AlunoResponse>> buscarPorNome(
            @RequestParam String nome
    ) {
        return ResponseEntity.ok(alunoServico.buscarPorNome(nome));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlunoResponse> buscarPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(alunoServico.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<AlunoResponse> criar(
            @RequestBody AlunoRequest request
    ) {
        AlunoResponse resposta = alunoServico.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(
            @PathVariable Long id,
            @RequestBody AlunoRequest request
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