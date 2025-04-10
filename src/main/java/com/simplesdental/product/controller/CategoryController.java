package com.simplesdental.product.controller;

import com.simplesdental.product.dto.CategoryDTO;
import com.simplesdental.product.utils.doc.CategoryApiResponses.*;
import com.simplesdental.product.dto.PaginationDTO;
import com.simplesdental.product.model.Category;
import com.simplesdental.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categorias", description = "Gerenciamento de categorias de produtos")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(summary = "Recuperar lista paginada de categorias", description = "Retorna uma lista paginada de categorias")
    @SwaggerResponseGetSuccess
    public PaginationDTO<Category> getAllCategories(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return categoryService.findAllPaginated(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar categoria por identificador único", description = "Retorna os dados de uma categoria existente pelo ID")
    @SwaggerResponseGetById
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return categoryService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar uma nova categoria de produto", description = "Cria e retorna uma nova categoria")
    @SwaggerResponseCreateSuccess
    @SwaggerResponseValidationError
    public Category createCategory(
            @Valid
            @RequestBody
            Category category
    ) {
        return categoryService.save(category);
    }
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma categoria existente", description = "Atualiza os dados de uma categoria existente pelo ID")
    @SwaggerResponseUpdateSuccess
    @SwaggerResponseValidationError
    @SwaggerResponseNotFound
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable Long id,
            @Valid
            @RequestBody Category category
    ) {
        return categoryService.findById(id)
                .map(existingCategory -> {
                    category.setId(id);
                    Category updated = categoryService.save(category);
                    CategoryDTO dto = new CategoryDTO(updated.getId(), updated.getName(), updated.getDescription());
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover uma categoria pelo seu identificador", description = "Exclui a categoria especificada pelo ID")
    @SwaggerResponseDeleteSuccess
    @SwaggerResponseNotFound
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        return categoryService.findById(id)
                .map(category -> {
                    categoryService.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
