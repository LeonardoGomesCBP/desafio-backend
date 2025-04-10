package com.simplesdental.product.utils.doc;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class UserApiResponses {

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                [
                  {
                    "id": 1,
                    "email": "usuario@email.com",
                    "role": "ADMIN"
                  }
                ]
                """)))
    })
    public @interface SwaggerResponseGetAll {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                  "id": 1,
                  "email": "usuario@email.com",
                  "role": "ADMIN"
                }
                """))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    public @interface SwaggerResponseGetById {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                  "id": 1,
                  "email": "usuario@email.com",
                  "role": "ADMIN"
                }
                """))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou e-mail já cadastrado", content = @Content)
    })
    public @interface SwaggerResponseCreate {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                  "id": 1,
                  "email": "usuario@email.com",
                  "role": "ADMIN"
                }
                """))),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou e-mail duplicado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    public @interface SwaggerResponseUpdate {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    public @interface SwaggerResponseDelete {}
}
