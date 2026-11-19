package br.edu.ceuma.semana2.repositorio;

import br.edu.ceuma.semana2.entidade.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AlunoRepositorio extends JpaRepository<Aluno, Long>, JpaSpecificationExecutor<Aluno> {
}

