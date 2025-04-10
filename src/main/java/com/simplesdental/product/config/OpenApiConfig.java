package com.simplesdental.product.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Produtos SimpleDental")
                        .version("1.0")
                        .description("API para gerenciamento de produtos, categorias e usuários")
                        .contact(new Contact().name("Criador").email("leonardogomescbp@gmail.com")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .servers(List.of(
                        new Server().url("/").description("Default Server URL")
                ));
    }


    @Bean
    public OpenApiCustomizer globalResponsesToAllEndpointsCustomizer() {
        return openApi -> {
            openApi.getPaths().values().forEach(pathItem -> {
                pathItem.readOperations().forEach(operation -> {
                    if (operation.getSecurity() != null && !operation.getSecurity().isEmpty()) {
                        if (!operation.getResponses().containsKey("401")) {
                            operation.getResponses().addApiResponse("401",
                                    new io.swagger.v3.oas.models.responses.ApiResponse()
                                            .description("Não autenticado"));
                        }

                        if (!operation.getResponses().containsKey("403")) {
                            operation.getResponses().addApiResponse("403",
                                    new io.swagger.v3.oas.models.responses.ApiResponse()
                                            .description("Acesso negado"));
                        }
                    }

                    if (operation.getParameters() != null &&
                            operation.getParameters().stream().anyMatch(p -> "path".equals(p.getIn()))) {
                        if (!operation.getResponses().containsKey("404")) {
                            operation.getResponses().addApiResponse("404",
                                    new io.swagger.v3.oas.models.responses.ApiResponse()
                                            .description("Recurso não encontrado"));
                        }
                    }
                });
            });
        };
    }
}