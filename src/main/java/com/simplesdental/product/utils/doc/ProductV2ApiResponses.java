package com.simplesdental.product.utils.doc;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class ProductV2ApiResponses {

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                   "content":   {
                                      "id": 1,
                                      "name": "Smartphone XYZ",
                                      "description": "Smartphone com 8GB RAM e 128GB de armazenamento",
                                      "price": 1299.99,
                                      "code": "PROD-001",
                                      "categoryId": 3,
                                      "category": {
                                      "modifiedAt": "2025-04-08T23:53:21.046Z",
                                      "createdAt": "2025-04-06T02:53:21.046Z",
                                      "id": 3,
                                      "name": "Smartphones",
                                      "description": "Telefones celulares e acessórios"
                                },
                  "page": 0,
                  "size": 10,
                  "totalElements": 100
                }
                """)))
    })
    public @interface SwaggerResponseGetAll {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                  "id": 1,
                  "code": 123,
                  "name": "Produto A",
                  "price": 100.0
                }
                """))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content)
    })
    public @interface SwaggerResponseGetById {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                  "id": 1,
                  "code": 123,
                  "name": "Produto A",
                  "price": 100.0
                }
                """))),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou código duplicado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    public @interface SwaggerResponseCreate {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                  "id": 1,
                  "code": 123,
                  "name": "Produto Atualizado",
                  "price": 120.0
                }
                """))),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou produto inexistente", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    public @interface SwaggerResponseUpdate {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produto excluído com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content)
    })
    public @interface SwaggerResponseDelete {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de produtos por categoria retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                 "content":   {
                                      "id": 1,
                                      "name": "Smartphone XYZ",
                                      "description": "Smartphone com 8GB RAM e 128GB de armazenamento",
                                      "price": 1299.99,
                                      "code": "PROD-001",
                                      "categoryId": 3,
                                      "category": {
                                      "modifiedAt": "2025-04-08T23:53:21.046Z",
                                      "createdAt": "2025-04-06T02:53:21.046Z",
                                      "id": 3,
                                      "name": "Smartphones",
                                      "description": "Telefones celulares e acessórios"
                                 },
                  "page": 0,
                  "size": 10,
                  "totalElements": 30
                }
                """)))
    })
    public @interface SwaggerResponseGetByCategory {}
}
