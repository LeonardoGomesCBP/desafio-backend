package com.simplesdental.product.controller;

import com.simplesdental.product.dto.ExceptionResponseDTO;
import com.simplesdental.product.dto.PaginationDTO;
import com.simplesdental.product.dto.ProductV2DTO;
import com.simplesdental.product.model.Product;
import com.simplesdental.product.service.ProductService;
import com.simplesdental.product.utils.PaginationUtils;
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
public class ProductControllerV2 {

    private final ProductService productService;

    @Autowired
    public ProductControllerV2(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public PaginationDTO<ProductV2DTO> getAllProducts(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductV2DTO> dtoPage = productService.findAll(pageable)
                .map(ProductV2DTO::fromEntity);

        return PaginationUtils.createPaginationDTO(dtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductV2DTO> getProductById(@PathVariable Long id) {
        return productService.findById(id)
                .map(ProductV2DTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductV2DTO productDTO) {
        try {
            Product product = productDTO.toEntity();
            Product savedProduct = productService.save(product);

            ProductV2DTO responseDTO = ProductV2DTO.fromEntityWithoutCategory(savedProduct);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (ExceptionResponseDTO e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductV2DTO productDTO) {
        try {
            Product product = productDTO.toEntity();
            Product updatedProduct = productService.update(id, product);
            return ResponseEntity.ok(ProductV2DTO.fromEntityWithoutCategory(updatedProduct));
        } catch (ExceptionResponseDTO e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        return productService.findById(id)
                .map(product -> {
                    productService.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categories/{categoryId}")
    public PaginationDTO<ProductV2DTO> getProductsByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ProductV2DTO> dtoPage = productService.findByCategoryId(categoryId, pageable)
                .map(product -> ProductV2DTO.fromEntity(product));

        return PaginationUtils.createPaginationDTO(dtoPage);
    }
}
