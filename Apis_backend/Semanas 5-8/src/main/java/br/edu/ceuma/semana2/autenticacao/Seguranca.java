package br.edu.ceuma.semana2.autenticacao;

import br.edu.ceuma.semana2.erro.ApiErro;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class Seguranca {
    @Bean
    SecretKey chaveJwt(@Value("${api.jwt.secret-base64}") String configuracao) {
        byte[] chave = Base64.getDecoder().decode(configuracao);
        if (chave.length < 32) {
            throw new IllegalStateException("Defina JWT_SECRET_BASE64 com pelo menos 32 bytes aleatórios em Base64.");
        }
        return new SecretKeySpec(chave, "HmacSHA256");
    }

    @Bean
    PasswordEncoder codificadorDeSenha() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    JwtEncoder emissor(SecretKey chaveJwt) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(chaveJwt));
    }

    @Bean
    JwtDecoder verificador(SecretKey chaveJwt, UsuarioRepositorio usuarios) {
        var decoder = NimbusJwtDecoder.withSecretKey(chaveJwt).macAlgorithm(MacAlgorithm.HS256).build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
            new JwtIssuerValidator(Sessoes.EMISSOR),
            new JwtTimestampValidator(Duration.ZERO),
            jwt -> validarConta(jwt, usuarios)));
        return decoder;
    }

    @Bean
    SecurityFilterChain filtros(HttpSecurity http, ObjectMapper json) throws Exception {
        var papeis = new JwtGrantedAuthoritiesConverter();
        papeis.setAuthoritiesClaimName("roles");
        papeis.setAuthorityPrefix("ROLE_");
        var conversor = new JwtAuthenticationConverter();
        conversor.setJwtGrantedAuthoritiesConverter(papeis);

        return http.csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sessao -> sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(rotas -> rotas
                .requestMatchers(HttpMethod.DELETE, "/usuarios/*", "/alunos/*").hasRole("ADMIN")
                .requestMatchers("/usuarios/**", "/perfil", "/alunos", "/alunos/**").authenticated()
                .anyRequest().permitAll())
            .exceptionHandling(erros -> erros
                .authenticationEntryPoint((req, res, e) -> naoAutenticado(res, json))
                .accessDeniedHandler((req, res, e) -> escrever(res, json, ApiErro.simples(403,
                    "FORBIDDEN", "Seu perfil não tem permissão para esta operação."))))
            .oauth2ResourceServer(oauth -> oauth
                .authenticationEntryPoint((req, res, e) -> naoAutenticado(res, json))
                .jwt(jwt -> jwt.jwtAuthenticationConverter(conversor)))
            .build();
    }

    private static OAuth2TokenValidatorResult validarConta(Jwt jwt, UsuarioRepositorio usuarios) {
        OAuth2Error invalido = new OAuth2Error("invalid_token", "Conta ou perfil fora de validade.", null);
        if (jwt.getSubject() == null || jwt.getExpiresAt() == null) {
            return OAuth2TokenValidatorResult.failure(invalido);
        }
        final long id;
        try {
            id = Long.parseLong(jwt.getSubject());
        } catch (NumberFormatException e) {
            return OAuth2TokenValidatorResult.failure(invalido);
        }
        return usuarios.findById(id)
            .filter(usuario -> List.of(usuario.getPerfil().name()).equals(jwt.getClaimAsStringList("roles")))
            .map(usuario -> OAuth2TokenValidatorResult.success())
            .orElseGet(() -> OAuth2TokenValidatorResult.failure(invalido));
    }

    private static void naoAutenticado(HttpServletResponse resposta, ObjectMapper json) throws IOException {
        resposta.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
        escrever(resposta, json, ApiErro.simples(401, "UNAUTHORIZED", "Autenticação necessária."));
    }

    private static void escrever(HttpServletResponse resposta, ObjectMapper json, ApiErro erro) throws IOException {
        resposta.setStatus(erro.status());
        resposta.setContentType(MediaType.APPLICATION_JSON_VALUE);
        json.writeValue(resposta.getOutputStream(), erro);
    }
}
