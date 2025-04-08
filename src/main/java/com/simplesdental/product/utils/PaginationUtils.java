package com.simplesdental.product.utils;

import com.simplesdental.product.dto.PaginationDTO;
import org.springframework.data.domain.Page;

public class PaginationUtils {
    public static <T> PaginationDTO<T> createPaginationDTO(Page<T> page) {
        return new PaginationDTO<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }
}