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
    private Long categoryId;
    private Category category;

    /**
     * Converts from Product to ProductDTO including the complete category
     */
    public static ProductDTO fromEntity(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCode() != null ? "PROD-" + String.format("%03d", product.getCode()) : null,
                product.getCategory() != null ? product.getCategory().getId() : null,
                product.getCategory()
        );
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
                null
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