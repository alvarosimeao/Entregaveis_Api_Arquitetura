package br.edu.ceuma.semana2.erro;

public class EntradaInvalida extends RuntimeException {
    public EntradaInvalida(String mensagem) {
        super(mensagem);
    }
}
