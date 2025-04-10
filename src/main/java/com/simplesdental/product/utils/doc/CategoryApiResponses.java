package com.simplesdental.product.utils.doc;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class CategoryApiResponses {

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "400",
                    description = "Erro de validação nos dados da categoria",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "timestamp": "2024-04-10 15:45:12",
                      "status": 400,
                      "path": "/api/categories",
                      "message": "name: não pode estar em branco"
                    }
                    """
                            )
                    )
            )
    })
    public @interface SwaggerResponseValidationError {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de categorias retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "content": [
                        {
                          "id": 1,
                          "name": "Categoria Exemplo",
                          "description": "Produtos eletrônicos e gadgets"

                        }
                      ],
                      "page": 0,
                      "size": 10,
                      "totalElements": 1
                    }
                    """
                            )
                    )
            )
    })
    public @interface SwaggerResponseGetSuccess {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Categoria encontrada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "id": 1,
                      "name": "Categoria Exemplo",
                      "description": "Produtos eletrônicos e gadgets"

                    }
                    """
                            )
                    )
            )
    })
    public @interface SwaggerResponseGetById {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Categoria criada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "id": 1,
                      "name": "Nova Categoria",
                      "description": "Produtos eletrônicos e gadgets"
                    }
                    """
                            )
                    )
            )
    })
    public @interface SwaggerResponseCreateSuccess {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Categoria atualizada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "id": 1,
                      "name": "Categoria Atualizada",
                      "description": "Produtos eletrônicos e gadgets"

                    }
                    """
                            )
                    )
            )
    })
    public @interface SwaggerResponseUpdateSuccess {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoria excluída com sucesso", content = @Content)
    })
    public @interface SwaggerResponseDeleteSuccess {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada", content = @Content)
    })
    public @interface SwaggerResponseNotFound {}
}
