package com.simplesdental.product.service;

import com.simplesdental.product.dto.PaginationDTO;
import com.simplesdental.product.dto.ProductDTO;
import com.simplesdental.product.model.Product;
import com.simplesdental.product.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final Logger logger = LoggerFactory.getLogger(ProductService.class);

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
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Caching(evict = {
            @CacheEvict(value = "product", key = "#id"),
            @CacheEvict(value = "productsPaginated", allEntries = true),
            @CacheEvict(value = "productsByCategory", allEntries = true)
    })
    public void deleteById(Long id) {
        productRepository.deleteById(id);
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