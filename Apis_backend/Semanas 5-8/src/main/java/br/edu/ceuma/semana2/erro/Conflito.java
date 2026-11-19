package br.edu.ceuma.semana2.erro;

public class Conflito extends RuntimeException {
    public Conflito(String mensagem) {
        super(mensagem);
    }
}
