package com.simplesdental.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplesdental.product.config.TestConfig;
import com.simplesdental.product.dto.PaginationDTO;
import com.simplesdental.product.dto.ProductDTO;
import com.simplesdental.product.model.Category;
import com.simplesdental.product.model.Product;
import com.simplesdental.product.service.JwtService;
import com.simplesdental.product.service.LoggingService;
import com.simplesdental.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestConfig.class)
@ActiveProfiles("test")
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product product;
    private ProductDTO productDTO;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("19.99"));
        product.setStatus(true);
        product.setCode(1);
        product.setCategory(category);

        productDTO = ProductDTO.fromEntity(product);
    }

    @Test
    void shouldGetAllProducts() throws Exception {
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(product));

        when(productService.findAll(any(Pageable.class))).thenReturn(productPage);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetProductById() throws Exception {
        when(productService.findById(1L)).thenReturn(Optional.of(product));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn404WhenGetProductByIdNotFound() throws Exception {
        when(productService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateProduct() throws Exception {
        when(productService.save(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        when(productService.update(eq(1L), any(Product.class))).thenReturn(product);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        when(productService.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productService).deleteById(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenDeleteProductNotFound() throws Exception {
        when(productService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetProductsByCategory() throws Exception {
        PaginationDTO<ProductDTO> paginationDTO = new PaginationDTO<>(
                Collections.singletonList(productDTO),
                0,
                10,
                1
        );

        when(productService.findByCategoryIdPaginated(eq(1L), any(Pageable.class))).thenReturn(paginationDTO);

        mockMvc.perform(get("/api/products/categories/1"))
                .andExpect(status().isOk());
    }
}