package br.edu.ceuma.semana2.erro;

import java.time.Instant;
import java.util.List;

public record ApiErro(int status, String error, String message, List<Campo> details, Instant timestamp) {
    public record Campo(String field, String message) {}

    public static ApiErro simples(int status, String error, String message) {
        return new ApiErro(status, error, message, List.of(), Instant.now());
    }
}
