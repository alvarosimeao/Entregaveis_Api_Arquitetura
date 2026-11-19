package br.edu.ceuma.semana2.servico;

import br.edu.ceuma.semana2.dto.AlunoRequest;
import br.edu.ceuma.semana2.dto.AlunoResponse;
import br.edu.ceuma.semana2.dto.Pagina;
import br.edu.ceuma.semana2.entidade.Aluno;
import br.edu.ceuma.semana2.erro.EntradaInvalida;
import br.edu.ceuma.semana2.erro.RecursoNaoEncontrado;
import br.edu.ceuma.semana2.repositorio.AlunoRepositorio;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Service
public class AlunoServico {
    private static final Set<String> ORDENACAO = Set.of("id", "nome", "idade", "matricula");

    private final AlunoRepositorio alunoRepositorio;

    public AlunoServico(AlunoRepositorio alunoRepositorio) {
        this.alunoRepositorio = alunoRepositorio;
    }

    @Transactional(readOnly = true)
    public Pagina<AlunoResponse> listar(int page, int limit, String sort, String order,
                                       String nome, Integer idadeMin, Integer idadeMax) {
        if (page < 1 || page > 1_000_000 || limit < 1 || limit > 100) {
            throw new EntradaInvalida("Informe page entre 1 e 1000000 e limit entre 1 e 100.");
        }
        if (!ORDENACAO.contains(sort) || !Set.of("asc", "desc").contains(order)
                || nome != null && nome.length() > 120
                || foraDaFaixa(idadeMin) || foraDaFaixa(idadeMax)
                || idadeMin != null && idadeMax != null && idadeMin > idadeMax) {
            throw new EntradaInvalida("Ordenação ou filtros inválidos.");
        }
        Sort ordenacao = Sort.by(Sort.Direction.fromString(order), sort);
        if (!sort.equals("id")) {
            ordenacao = ordenacao.and(Sort.by("id"));
        }
        Specification<Aluno> filtros = (root, query, cb) -> {
            var predicados = new ArrayList<Predicate>();
            if (nome != null && !nome.isBlank()) {
                String filtro = nome.strip().toLowerCase(Locale.ROOT)
                    .replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
                predicados.add(cb.like(cb.lower(root.get("nome")), "%" + filtro + "%", '\\'));
            }
            if (idadeMin != null) {
                predicados.add(cb.ge(root.get("idade"), idadeMin));
            }
            if (idadeMax != null) {
                predicados.add(cb.le(root.get("idade"), idadeMax));
            }
            return cb.and(predicados.toArray(Predicate[]::new));
        };
        var resultado = alunoRepositorio.findAll(filtros, PageRequest.of(page - 1, limit, ordenacao));
        return new Pagina<>(resultado.map(this::toResponse).getContent(),
            new Pagina.Meta(resultado.getTotalElements(), limit, page, resultado.getTotalPages()));
    }

    private boolean foraDaFaixa(Integer idade) {
        return idade != null && (idade < 1 || idade > 130);
    }

    public AlunoResponse criar(AlunoRequest request) {
        Aluno aluno = new Aluno();
        aluno.setNome(request.getNome().strip());
        aluno.setIdade(request.getIdade());
        aluno.setMatricula(request.getMatricula());

        Aluno alunoSalvo = alunoRepositorio.save(aluno);
        return toResponse(alunoSalvo);
    }

    public AlunoResponse buscarPorId(Long id) {
        Optional<Aluno> optionalAluno = alunoRepositorio.findById(id);

        if (optionalAluno.isPresent()) {
            return toResponse(optionalAluno.get());
        } else {
            throw new RecursoNaoEncontrado("Aluno não encontrado.");
        }
    }

    @Transactional
    public void atualizar(Long id, AlunoRequest request) {
        Aluno aluno = alunoRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontrado("Aluno não encontrado."));

        aluno.setNome(request.getNome().strip());
        aluno.setIdade(request.getIdade());
        aluno.setMatricula(request.getMatricula());
        alunoRepositorio.save(aluno);
    }

    @Transactional
    public void excluir(Long id) {
        boolean existe = alunoRepositorio.existsById(id);

        if (!existe) {
            throw new RecursoNaoEncontrado("Aluno não encontrado.");
        }

        alunoRepositorio.deleteById(id);
    }

    private AlunoResponse toResponse(Aluno aluno) {
        return new AlunoResponse(
                aluno.getId(),
                aluno.getNome(),
                aluno.getIdade(),
                aluno.getMatricula()
        );
    }
}
