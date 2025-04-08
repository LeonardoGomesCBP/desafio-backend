package com.simplesdental.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationDTO<T> implements Serializable {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
}
