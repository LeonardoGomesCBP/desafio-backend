package com.simplesdental.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.simplesdental.product.model.Category;
import com.simplesdental.product.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductV2DTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer code;
    private Boolean status;
    private Category category;
    private Long categoryId;

    /**
     * Converts from Product to ProductV2DTO including the complete category
     */
    public static ProductV2DTO fromEntity(Product product) {
        return new ProductV2DTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCode(),
                product.getStatus(),
                product.getCategory()
        );
    }

    public ProductV2DTO(Long id, String name, String description, BigDecimal price,
                        Integer code, Boolean status, Category category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.code = code;
        this.status = status;
        this.category = category;
        this.categoryId = category != null ? category.getId() : null;
    }

    public static ProductV2DTO fromEntityWithoutCategory(Product product) {
        ProductV2DTO dto = new ProductV2DTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setCode(product.getCode());
        dto.setStatus(product.getStatus());

        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
        }

        return dto;
    }

    /**
     * Converts from ProductV2DTO to Product
     */
    public Product toEntity() {
        Product product = new Product();
        product.setId(this.id);
        product.setName(this.name);
        product.setDescription(this.description);
        product.setPrice(this.price);
        product.setCode(this.code);
        product.setStatus(this.status);

        if (this.category != null) {
            product.setCategory(this.category);
        } else if (this.categoryId != null) {
            Category cat = new Category();
            cat.setId(this.categoryId);
            product.setCategory(cat);
        }

        return product;
    }
}