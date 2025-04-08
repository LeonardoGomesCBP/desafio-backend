package com.simplesdental.product.service;

import com.simplesdental.product.dto.PaginationDTO;
import com.simplesdental.product.model.Category;
import com.simplesdental.product.repository.CategoryRepository;
import com.simplesdental.product.utils.PaginationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Cacheable(value = "categories", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + (#pageable.sort != null ? #pageable.sort.toString() : 'unsorted')")
    @Transactional(readOnly = true)
    public PaginationDTO<Category> findAllPaginated(Pageable pageable) {
        Page<Category> page = categoryRepository.findAll(pageable);
        return PaginationUtils.createPaginationDTO(page);
    }

    @Cacheable(value = "category", key = "#id")
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @CacheEvict(value = {"categories", "category"}, allEntries = true)
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @CacheEvict(value = {"categories", "category"}, allEntries = true)
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}