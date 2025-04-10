package com.simplesdental.product.controller;

import com.simplesdental.product.dto.ExceptionResponse;
import com.simplesdental.product.dto.PaginationDTO;
import com.simplesdental.product.dto.ProductV2DTO;
import com.simplesdental.product.model.Product;
import com.simplesdental.product.service.ProductService;
import com.simplesdental.product.utils.PaginationUtils;
import com.simplesdental.product.utils.doc.ProductV2ApiResponses.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/products")
@Tag(name = "Produtos (v2)", description = "Gerenciamento de produtos - versão 2")
public class ProductControllerV2 {

    private final ProductService productService;

    @Autowired
    public ProductControllerV2(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Listar produtos (v2)", description = "Retorna uma lista paginada de produtos - versão 2")
    @SwaggerResponseGetAll
    public PaginationDTO<ProductV2DTO> getAllProducts(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductV2DTO> dtoPage = productService.findAll(pageable)
                .map(ProductV2DTO::fromEntity);
        return PaginationUtils.createPaginationDTO(dtoPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID (v2)", description = "Retorna os dados de um produto pelo seu ID - versão 2")
    @SwaggerResponseGetById
    public ResponseEntity<ProductV2DTO> getProductById(@PathVariable Long id) {
        return productService.findById(id)
                .map(ProductV2DTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar produto (v2)", description = "Cria um novo produto com os dados fornecidos - versão 2")
    @SwaggerResponseCreate
    public ResponseEntity<?> createProduct(
            @Valid
            @RequestBody
            ProductV2DTO productDTO
    ) {
        try {
            Product product = productDTO.toEntity();
            Product savedProduct = productService.save(product);
            ProductV2DTO responseDTO = ProductV2DTO.fromEntityWithoutCategory(savedProduct);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (ExceptionResponse e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar produto (v2)", description = "Atualiza os dados de um produto existente - versão 2")
    @SwaggerResponseUpdate
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @Valid
            @RequestBody
            ProductV2DTO productDTO
    ) {
        try {
            Product product = productDTO.toEntity();
            Product updatedProduct = productService.update(id, product);
            return ResponseEntity.ok(ProductV2DTO.fromEntityWithoutCategory(updatedProduct));
        } catch (ExceptionResponse e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir produto (v2)", description = "Remove um produto do sistema pelo ID - versão 2")
    @SwaggerResponseDelete
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        return productService.findById(id)
                .map(product -> {
                    productService.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categories/{categoryId}")
    @Operation(summary = "Listar produtos por categoria (v2)", description = "Retorna produtos da categoria informada - versão 2")
    @SwaggerResponseGetByCategory
    public PaginationDTO<ProductV2DTO> getProductsByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ProductV2DTO> dtoPage = productService.findByCategoryId(categoryId, pageable)
                .map(ProductV2DTO::fromEntity);

        return PaginationUtils.createPaginationDTO(dtoPage);
    }
}
