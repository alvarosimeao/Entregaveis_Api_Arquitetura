package br.edu.ceuma.semana2.autenticacao;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Documentacao {
    @Bean
    OpenAPI definicao() {
        return new OpenAPI()
            .info(new Info().title("API acadêmica de alunos").version("1.0")
                .description("CRUD de demonstração, paginação e acesso por perfil. Use somente dados fictícios."))
            .components(new Components().addSecuritySchemes("bearerAuth",
                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
    }
}
