package br.edu.ceuma.semana2.autenticacao;

import br.edu.ceuma.semana2.erro.Conflito;
import br.edu.ceuma.semana2.erro.CredenciaisInvalidas;
import br.edu.ceuma.semana2.erro.EntradaInvalida;
import br.edu.ceuma.semana2.erro.RecursoNaoEncontrado;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class Sessoes {
    static final String EMISSOR = "api-academica-alunos";
    private static final int DURACAO_SEGUNDOS = 900;
    private final UsuarioRepositorio usuarios;
    private final PasswordEncoder senhas;
    private final JwtEncoder tokens;
    private final String hashDeComparacao;

    public Sessoes(UsuarioRepositorio usuarios, PasswordEncoder senhas, JwtEncoder tokens) {
        this.usuarios = usuarios;
        this.senhas = senhas;
        this.tokens = tokens;
        this.hashDeComparacao = senhas.encode(UUID.randomUUID().toString());
    }

    @Transactional
    public Usuario cadastrar(String nome, String email, String senha) {
        validarTamanhoSenha(senha);
        String emailNormalizado = normalizarEmail(email);
        if (usuarios.existsByEmail(emailNormalizado)) {
            throw new Conflito("E-mail já cadastrado.");
        }
        return usuarios.saveAndFlush(new Usuario(nome.strip(), emailNormalizado, senhas.encode(senha)));
    }

    @Transactional(readOnly = true)
    public Token entrar(String email, String senha) {
        validarTamanhoSenha(senha);
        Usuario usuario = usuarios.findByEmail(normalizarEmail(email)).orElse(null);
        String hash = usuario == null ? hashDeComparacao : usuario.getSenhaHash();
        if (!senhas.matches(senha, hash) || usuario == null) {
            throw new CredenciaisInvalidas();
        }
        Instant agora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder().issuer(EMISSOR).subject(usuario.getId().toString())
            .issuedAt(agora).expiresAt(agora.plusSeconds(DURACAO_SEGUNDOS))
            .claim("roles", List.of(usuario.getPerfil().name())).build();
        var parametros = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims);
        return new Token(tokens.encode(parametros).getTokenValue(), "Bearer", DURACAO_SEGUNDOS);
    }

    @Transactional(readOnly = true)
    public Usuario perfil(long id) {
        return usuarios.findById(id).orElseThrow(CredenciaisInvalidas::new);
    }

    @Transactional
    public void excluir(long id) {
        Usuario usuario = usuarios.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontrado("Usuário não encontrado."));
        usuarios.delete(usuario);
    }

    private static String normalizarEmail(String email) {
        return email.strip().toLowerCase(Locale.ROOT);
    }

    private static void validarTamanhoSenha(String senha) {
        if (senha.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new EntradaInvalida("A senha deve ocupar até 72 bytes em UTF-8.");
        }
    }

    public record Token(String token, String tokenType, int expiresIn) {}
}
