package com.simplesdental.product.controller;

import com.simplesdental.product.dto.ExceptionResponseDTO;
import com.simplesdental.product.dto.PaginationDTO;
import com.simplesdental.product.dto.ProductDTO;
import com.simplesdental.product.model.Product;
import com.simplesdental.product.service.LoggingService;
import com.simplesdental.product.service.ProductService;
import com.simplesdental.product.utils.PaginationUtils;
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
public class ProductController {

    private final ProductService productService;
    private final Logger logger = LoggingService.getLogger(ProductController.class);

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public PaginationDTO<ProductDTO> getAllProducts(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        if (logger.isDebugEnabled()) {
            logger.debug("Getting all products with pagination: {}", pageable);
        }

        Page<ProductDTO> productDtoPage = productService.findAll(pageable)
                .map(ProductDTO::fromEntity);

        return PaginationUtils.createPaginationDTO(productDtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        if (logger.isDebugEnabled()) {
            logger.debug("Getting product by ID: {}", id);
        }

        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        logger.info("Creating new product: {}", productDTO.getName());

        try {
            Product product = productDTO.toEntity();
            Product savedProduct = productService.save(product);

            ProductDTO responseDTO = ProductDTO.fromEntityWithoutCategory(savedProduct);
            logger.info("Product created successfully: {}", savedProduct.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (ExceptionResponseDTO e) {
            logger.warn("Product creation failed: {} - {}", productDTO.getName(), e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Product creation error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody Product product) {
        logger.info("Updating product: {}", id);

        try {
            Product updatedProduct = productService.update(id, product);
            logger.info("Product updated successfully: {}", id);
            return ResponseEntity.ok(ProductDTO.fromEntityWithoutCategory(updatedProduct));
        } catch (ExceptionResponseDTO e) {
            logger.warn("Product update failed: {} - {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Product update error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        logger.info("Deleting product: {}", id);

        return productService.findById(id)
                .map(product -> {
                    productService.deleteById(id);
                    logger.info("Product deleted successfully: {}", id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categories/{categoryId}")
    public PaginationDTO<ProductDTO> getProductsByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        if (logger.isDebugEnabled()) {
            logger.debug("Getting products by category: {}", categoryId);
        }

        return productService.findByCategoryIdPaginated(categoryId, pageable);
    }
}