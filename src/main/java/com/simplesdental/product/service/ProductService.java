package com.simplesdental.product.service;

import com.simplesdental.product.dto.ExceptionResponseDTO;
import com.simplesdental.product.dto.PaginationDTO;
import com.simplesdental.product.dto.ProductDTO;
import com.simplesdental.product.model.Product;
import com.simplesdental.product.repository.ProductRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final Logger logger = LoggingService.getLogger(ProductService.class);

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Cacheable(value = "productsPaginated", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort")
    @Transactional(readOnly = true)
    public PaginationDTO<Product> findAllPaginated(Pageable pageable) {
        if (logger.isDebugEnabled()) {
            logger.debug("Cache miss for paginated products");
        }

        Page<Product> page = productRepository.findAll(pageable);

        return new PaginationDTO<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }

    @Cacheable(value = "product", key = "#id")
    public Optional<Product> findById(Long id) {
        return productRepository.findByIdWithCategory(id);
    }

    @Caching(evict = {
            @CacheEvict(value = "product", key = "#product.id", condition = "#product.id != null"),
            @CacheEvict(value = "productsPaginated", allEntries = true),
            @CacheEvict(value = "productsByCategory", allEntries = true)
    })
    public Product save(Product product) throws Exception {
        long startTime = System.currentTimeMillis();
        String operationId = LoggingService.startOperation(logger, "product_save");

        try {
            LoggingService.logWithFields(logger, "INFO", "Creating new product",
                    Map.of(
                            "productName", product.getName(),
                            "categoryId", product.getCategory() != null ? product.getCategory().getId() : "null"
                    ));

            if (product.getCode() != null && productRepository.existsByCode(product.getCode())) {
                LoggingService.logWithField(logger, "WARN", "Product with duplicate code", "code", product.getCode());
                throw new ExceptionResponseDTO("Produto com o código '" + product.getCode() + "' já existe.");
            }

            if (product.getStatus() == null) {
                product.setStatus(true);
            }

            Product savedProduct = productRepository.save(product);

            LoggingService.logWithFields(logger, "INFO", "Product saved successfully",
                    Map.of(
                            "productId", savedProduct.getId(),
                            "productName", savedProduct.getName()
                    ));

            return savedProduct;

        } catch (Exception e) {
            LoggingService.logError(logger, "product_save", e);
            throw e;
        } finally {
            LoggingService.endOperation(logger, "product_save", startTime);
        }
    }

    public Product update(Long id, Product product) throws Exception {
        long startTime = System.currentTimeMillis();
        String operationId = LoggingService.startOperation(logger, "product_update");

        try {
            LoggingService.logWithFields(logger, "INFO", "Updating product",
                    Map.of(
                            "productId", id,
                            "productName", product.getName()
                    ));

            Optional<Product> existingProductOpt = productRepository.findById(id);
            if (!existingProductOpt.isPresent()) {
                LoggingService.logWithField(logger, "WARN", "Product not found for update", "productId", id);
                throw new ExceptionResponseDTO("Produto não encontrado com ID: " + id);
            }

            Product existingProduct = existingProductOpt.get();

            if (product.getCode() != null &&
                    !product.getCode().equals(existingProduct.getCode()) &&
                    productRepository.existsByCode(product.getCode())) {
                LoggingService.logWithFields(logger, "WARN", "Attempt to update product with already existing code",
                        Map.of(
                                "productId", id,
                                "existingCode", existingProduct.getCode(),
                                "newCode", product.getCode()
                        ));
                throw new ExceptionResponseDTO("Produto com o código '" + product.getCode() + "' já existe.");
            }

            product.setId(id);

            if (product.getStatus() == null) {
                product.setStatus(existingProduct.getStatus() != null ? existingProduct.getStatus() : true);
            }

            Product updatedProduct = productRepository.save(product);

            LoggingService.logWithFields(logger, "INFO", "Product updated successfully",
                    Map.of(
                            "productId", updatedProduct.getId(),
                            "productName", updatedProduct.getName()
                    ));

            return updatedProduct;

        } catch (Exception e) {
            LoggingService.logError(logger, "product_update", e);
            throw e;
        } finally {
            LoggingService.endOperation(logger, "product_update", startTime);
        }
    }

    @Caching(evict = {
            @CacheEvict(value = "product", key = "#id"),
            @CacheEvict(value = "productsPaginated", allEntries = true),
            @CacheEvict(value = "productsByCategory", allEntries = true)
    })
    public void deleteById(Long id) {
        logger.info("Deleting product: {}", id);

        try {
            productRepository.deleteById(id);
            logger.info("Product deleted successfully: {}", id);
        } catch (Exception e) {
            logger.error("Failed to delete product: {}", id, e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Page<Product> findByCategoryId(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable);
    }

    @Cacheable(value = "productsByCategory", key = "#categoryId + '-' + #pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort")
    @Transactional(readOnly = true)
    public PaginationDTO<ProductDTO> findByCategoryIdPaginated(Long categoryId, Pageable pageable) {
        Page<ProductDTO> productPage = productRepository.findByCategoryId(categoryId, pageable)
                .map(ProductDTO::fromEntity);

        return new PaginationDTO<>(
                productPage.getContent(),
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements()
        );
    }
}