package br.edu.ceuma.semana2.autenticacao;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
public class AcessoControlador {
    private final Sessoes sessoes;

    public AcessoControlador(Sessoes sessoes) {
        this.sessoes = sessoes;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public PerfilResposta registrar(@Valid @RequestBody Cadastro dados) {
        return PerfilResposta.de(sessoes.cadastrar(dados.nome(), dados.email(), dados.senha()));
    }

    @PostMapping("/login")
    public Sessoes.Token entrar(@Valid @RequestBody Login dados) {
        return sessoes.entrar(dados.email(), dados.senha());
    }

    @GetMapping("/perfil")
    @SecurityRequirement(name = "bearerAuth")
    public PerfilResposta perfil(@AuthenticationPrincipal Jwt autenticacao) {
        return PerfilResposta.de(sessoes.perfil(Long.parseLong(autenticacao.getSubject())));
    }

    @DeleteMapping("/usuarios/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "bearerAuth")
    public void excluir(@PathVariable long id) {
        sessoes.excluir(id);
    }

    public record Cadastro(
        @NotBlank @Size(max = 120) String nome,
        @NotBlank @Email @Size(max = 160) String email,
        @NotBlank @Size(min = 8, max = 72) String senha
    ) {}

    public record Login(
        @NotBlank @Email @Size(max = 160) String email,
        @NotBlank @Size(max = 72) String senha
    ) {}

    public record PerfilResposta(Long id, String nome, String email, Usuario.Perfil perfil) {
        static PerfilResposta de(Usuario usuario) {
            return new PerfilResposta(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getPerfil());
        }
    }
}
