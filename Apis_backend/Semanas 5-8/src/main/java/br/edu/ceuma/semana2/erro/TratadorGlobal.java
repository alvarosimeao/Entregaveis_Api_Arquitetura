package br.edu.ceuma.semana2.erro;

import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class TratadorGlobal {
    private static final Logger LOG = LoggerFactory.getLogger(TratadorGlobal.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiErro> validacao(MethodArgumentNotValidException erro) {
        var detalhes = erro.getBindingResult().getFieldErrors().stream()
            .map(campo -> new ApiErro.Campo(campo.getField(), campo.getDefaultMessage()))
            .distinct().toList();
        return resposta(new ApiErro(400, "VALIDATION_ERROR", "Os dados informados são inválidos.", detalhes, Instant.now()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiErro> jsonInvalido() {
        return resposta(ApiErro.simples(400, "MALFORMED_JSON", "O corpo da requisição é inválido."));
    }

    @ExceptionHandler(EntradaInvalida.class)
    ResponseEntity<ApiErro> entradaInvalida(EntradaInvalida erro) {
        return resposta(ApiErro.simples(400, "VALIDATION_ERROR", erro.getMessage()));
    }

    @ExceptionHandler(RecursoNaoEncontrado.class)
    ResponseEntity<ApiErro> recursoAusente(RecursoNaoEncontrado erro) {
        return resposta(ApiErro.simples(404, "NOT_FOUND", erro.getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<ApiErro> parametroInvalido(MethodArgumentTypeMismatchException erro) {
        return resposta(ApiErro.simples(400, "VALIDATION_ERROR", "Parâmetro inválido: " + erro.getName() + "."));
    }

    @ExceptionHandler(Conflito.class)
    ResponseEntity<ApiErro> conflito(Conflito erro) {
        return resposta(ApiErro.simples(409, "CONFLICT", erro.getMessage()));
    }

    @ExceptionHandler(CredenciaisInvalidas.class)
    ResponseEntity<ApiErro> credenciaisInvalidas() {
        return resposta(ApiErro.simples(401, "UNAUTHORIZED", "Credenciais inválidas."));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApiErro> integridade() {
        return resposta(ApiErro.simples(409, "CONFLICT", "O registro conflita com os dados existentes."));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiErro> demaisFalhas(Exception erro) {
        if (erro instanceof ErrorResponse esperado && esperado.getStatusCode().is4xxClientError()) {
            int status = esperado.getStatusCode().value();
            String codigo = status == 404 ? "NOT_FOUND" : status == 400 ? "VALIDATION_ERROR" : "REQUEST_ERROR";
            String mensagem = status == 404 ? "Rota não encontrada." : "A requisição não pôde ser processada.";
            return ResponseEntity.status(status).headers(esperado.getHeaders()).body(ApiErro.simples(status, codigo, mensagem));
        }
        LOG.error("Falha interna na API: {}", erro.getClass().getSimpleName());
        return resposta(ApiErro.simples(500, "INTERNAL_ERROR", "Erro interno no servidor."));
    }

    private ResponseEntity<ApiErro> resposta(ApiErro erro) {
        return ResponseEntity.status(erro.status()).body(erro);
    }
}
