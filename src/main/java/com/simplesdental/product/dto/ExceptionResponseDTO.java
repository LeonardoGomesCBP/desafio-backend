package com.simplesdental.product.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExceptionResponseDTO extends RuntimeException {
    public ExceptionResponseDTO(String message) {
        super(message);
    }
}