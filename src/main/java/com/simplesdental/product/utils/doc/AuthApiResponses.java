package com.simplesdental.product.utils.doc;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;

import java.lang.annotation.*;

public class AuthApiResponses {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Login realizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"token\": \"eyJhbGci...\"}")
                    )
            )
    })
    public @interface SwaggerResponseLoginSuccess {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Registro realizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"token\": \"eyJhbGci...\"}")
                    )
            )
    })
    public @interface SwaggerResponseRegisterSuccess {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "400",
                    description = "Erro de validação nos dados enviados",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                  "timestamp": "2024-04-10 15:45:12",
                  "status": 400,
                  "path": "/auth/endpoint",
                  "message": "email: deve ser um e-mail válido; password: mínimo 8 caracteres"
                }
                """)
                    )
            )
    })
    public @interface SwaggerResponseValidationError {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "409",
                    description = "Regra de negócio violada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                  "timestamp": "2024-04-10T15:45:12",
                  "message": "Usuário já registrado com esse e-mail.",
                  "path": "/auth/register"
                }
                """)
                    )
            )
    })
    public @interface SwaggerResponseBusinessError {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Senha atualizada com sucesso", content = @Content)
    })
    public @interface SwaggerResponsePasswordUpdated {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content)
    })
    public @interface SwaggerResponseUnauthorized {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário autenticado encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"id\": 1, \"email\": \"user@email.com\"}")
                    )
            )
    })
    public @interface SwaggerResponseContextSuccess {}
}
