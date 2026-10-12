package com.daniel_p_d.extractor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

// Configuração da documentação
@Configuration 
public class OpenApiConfig {
    private static final String ESQUEMA_JWT = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Extractor: Plataforma de Coleta e Análise de Vagas de Emprego para Apoio à Tomada de Decisão Profissional.")
                .version("1.0")
                .description("Coleta vagas de portais de emprego (InfoJobs, Catho e Vagas.com), "
                    + "extrai as tecnologias citadas e disponibiliza análises por categoria. "
                    + "Para testar os endpoints protegidos, faça login em POST /auth/login, "
                    + "copie o token e informe-o no botão Authorize (sem o prefixo 'Bearer')."))
            .components(new Components().addSecuritySchemes(ESQUEMA_JWT,
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")))
            .addSecurityItem(new SecurityRequirement().addList(ESQUEMA_JWT));
    }
}


