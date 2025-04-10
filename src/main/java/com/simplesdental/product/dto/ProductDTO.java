package com.simplesdental.product.dto;

import com.simplesdental.product.model.Category;
import com.simplesdental.product.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDTO implements Serializable {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String code;
    private Boolean status;
    private Long categoryId;
    private Category category;

    /**
     * Construtor específico para o método fromEntityWithoutCategory
     */
    public ProductDTO(
            Long id,
            String name,
            String description,
            BigDecimal price,
            String code,
            Long categoryId,
            Boolean status
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.code = code;
        this.categoryId = categoryId;
        this.status = status;
    }

    /**
     * Converts from Product to ProductDTO including the complete category
     */
    public static ProductDTO fromEntity(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setCode(product.getCode() != null ? "PROD-" + String.format("%03d", product.getCode()) : null);
        dto.setStatus(product.getStatus());

        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategory(product.getCategory());
        }

        return dto;
    }

    /**
     * Converts from Product to ProductDTO without including the complete category (only the ID)
     */
    public static ProductDTO fromEntityWithoutCategory(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCode() != null ? "PROD-" + String.format("%03d", product.getCode()) : null,
                product.getCategory() != null ? product.getCategory().getId() : null,
                product.getStatus()
        );
    }

    /**
     * Converts from ProductDTO to Product
     */
    public Product toEntity() {
        Product product = new Product();
        product.setId(this.id);
        product.setName(this.name);
        product.setDescription(this.description);
        product.setPrice(this.price);
        product.setStatus(this.status);

        if (this.code != null && this.code.startsWith("PROD-")) {
            try {
                Integer numericCode = Integer.parseInt(this.code.substring(5));
                product.setCode(numericCode);
            } catch (NumberFormatException e) {
                throw new ExceptionResponse("Código inválido: " + this.code);
            }
        }

        if (this.categoryId != null) {
            Category category = new Category();
            category.setId(this.categoryId);
            product.setCategory(category);
        } else if (this.category != null) {
            product.setCategory(this.category);
        }

        return product;
    }
}