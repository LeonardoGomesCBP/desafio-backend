package com.simplesdental.product.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "category")
@EqualsAndHashCode(exclude = "category")
public class Product extends AbstractEntity<Long> {

    @NotBlank(message = "Nome não pode ser vazio.")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres.")
    private String name;

    @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres.")
    private String description;

    @NotNull(message = "Preço não pode ser nulo.")
    @Positive(message = "Preço deve ser positivo")
    private BigDecimal price;

    @NotNull(message = "Status não pode ser nulo")
    private Boolean status;

    @NotNull(message = "Código não pode ser nulo")
    @Column(unique = true)
    private Integer code;

    @NotNull(message = "Categoria não pode ser nula.")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties({"products"})
    private Category category;
}