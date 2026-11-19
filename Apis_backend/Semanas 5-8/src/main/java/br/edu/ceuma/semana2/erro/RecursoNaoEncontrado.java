package br.edu.ceuma.semana2.erro;

public class RecursoNaoEncontrado extends RuntimeException {
    public RecursoNaoEncontrado(String mensagem) {
        super(mensagem);
    }
}
