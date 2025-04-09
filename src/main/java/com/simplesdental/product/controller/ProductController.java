package com.simplesdental.product.controller;

import com.simplesdental.product.dto.ExceptionResponseDTO;
import com.simplesdental.product.dto.PaginationDTO;
import com.simplesdental.product.dto.ProductDTO;
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
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public PaginationDTO<ProductDTO> getAllProducts(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProductDTO> productDtoPage = productService.findAll(pageable)
                .map(ProductDTO::fromEntity);

        return PaginationUtils.createPaginationDTO(productDtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        try {

            Product product = productDTO.toEntity();
            Product savedProduct = productService.save(product);

            ProductDTO responseDTO = ProductDTO.fromEntityWithoutCategory(savedProduct);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (ExceptionResponseDTO e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno: " + e.getMessage());
        }
    }




    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody Product product) {
        try {
            Product updatedProduct = productService.update(id, product);
            return ResponseEntity.ok(ProductDTO.fromEntityWithoutCategory(updatedProduct));
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
    public PaginationDTO<ProductDTO> getProductsByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return productService.findByCategoryIdPaginated(categoryId, pageable);
    }
}