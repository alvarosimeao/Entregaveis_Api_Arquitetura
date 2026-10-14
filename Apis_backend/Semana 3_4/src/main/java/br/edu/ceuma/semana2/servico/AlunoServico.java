package br.edu.ceuma.semana2.servico;

import br.edu.ceuma.semana2.dto.AlunoRequest;
import br.edu.ceuma.semana2.dto.AlunoResponse;
import br.edu.ceuma.semana2.entidade.Aluno;
import br.edu.ceuma.semana2.repositorio.AlunoRepositorio;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AlunoServico {

    private final AlunoRepositorio alunoRepositorio;

    public AlunoServico(AlunoRepositorio alunoRepositorio) {
        this.alunoRepositorio = alunoRepositorio;
    }

    public List<AlunoResponse> listarTodos() {
        List<Aluno> alunos = alunoRepositorio.findAll();
        List<AlunoResponse> respostas = new ArrayList<>();

        for (Aluno aluno : alunos) {
            respostas.add(toResponse(aluno));
        }

        return respostas;
    }

    public List<AlunoResponse> buscarPorNome(String nome) {
        List<Aluno> alunos = alunoRepositorio.findByNome(nome);
        List<AlunoResponse> respostas = new ArrayList<>();

        for (Aluno aluno : alunos) {
            respostas.add(toResponse(aluno));
        }

        return respostas;
    }

    public AlunoResponse criar(AlunoRequest request) {
        Aluno aluno = new Aluno();
        aluno.setNome(request.getNome());
        aluno.setIdade(request.getIdade());

        Aluno alunoSalvo = alunoRepositorio.save(aluno);
        return toResponse(alunoSalvo);
    }

    public AlunoResponse buscarPorId(Long id) {
        Optional<Aluno> optionalAluno = alunoRepositorio.findById(id);

        if (optionalAluno.isPresent()) {
            return toResponse(optionalAluno.get());
        } else {
            throw new RuntimeException("Aluno não encontrado com o ID: " + id);
        }
    }

    public void atualizar(Long id, AlunoRequest request) {
        Aluno aluno = alunoRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Aluno não encontrado com o ID: " + id));

        aluno.setNome(request.getNome());
        aluno.setIdade(request.getIdade());
        alunoRepositorio.save(aluno);
    }

    public void excluir(Long id) {
        boolean existe = alunoRepositorio.existsById(id);

        if (!existe) {
            throw new RuntimeException("Aluno não encontrado com o ID: " + id);
        }

        alunoRepositorio.deleteById(id);
    }

    private AlunoResponse toResponse(Aluno aluno) {
        return new AlunoResponse(
                aluno.getId(),
                aluno.getNome(),
                aluno.getIdade()
        );
    }
}