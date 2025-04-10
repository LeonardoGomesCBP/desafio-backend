package com.simplesdental.product.controller;

import com.simplesdental.product.dto.ExceptionResponse;
import com.simplesdental.product.dto.PaginationDTO;
import com.simplesdental.product.dto.ProductDTO;
import com.simplesdental.product.model.Product;
import com.simplesdental.product.service.LoggingService;
import com.simplesdental.product.service.ProductService;
import com.simplesdental.product.utils.PaginationUtils;
import com.simplesdental.product.utils.doc.ProductApiResponses.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Produtos", description = "Gerenciamento de produtos")
public class ProductController {

    private final ProductService productService;
    private final Logger logger = LoggingService.getLogger(ProductController.class);

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Listar produtos", description = "Retorna uma lista paginada de produtos")
    @SwaggerResponseGetAll
    public PaginationDTO<ProductDTO> getAllProducts(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductDTO> productDtoPage = productService.findAll(pageable).map(ProductDTO::fromEntity);
        return PaginationUtils.createPaginationDTO(productDtoPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID", description = "Retorna os dados de um produto pelo seu identificador")
    @SwaggerResponseGetById
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar produto", description = "Cria um novo produto com os dados fornecidos")
    @SwaggerResponseCreate
    public ResponseEntity<ProductDTO> createProduct(
            @Valid @RequestBody ProductDTO productDTO
    ) {
        Product product = productDTO.toEntity();
        Product savedProduct = productService.save(product);
        ProductDTO responseDTO = ProductDTO.fromEntityWithoutCategory(savedProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }


    @PutMapping("/{id}")
    @Operation(summary = "Atualizar produto", description = "Atualiza os dados de um produto existente")
    @SwaggerResponseUpdate
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody Product product
    ) throws Exception {
        Product updatedProduct = productService.update(id, product);
        return ResponseEntity.ok(ProductDTO.fromEntityWithoutCategory(updatedProduct));
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir produto", description = "Remove um produto do sistema pelo ID")
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
    @Operation(summary = "Listar produtos por categoria", description = "Retorna produtos pertencentes à categoria informada")
    @SwaggerResponseGetByCategory
    public PaginationDTO<ProductDTO> getProductsByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return productService.findByCategoryIdPaginated(categoryId, pageable);
    }
}
