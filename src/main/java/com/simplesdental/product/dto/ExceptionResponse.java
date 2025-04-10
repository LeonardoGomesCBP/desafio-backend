package com.simplesdental.product.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExceptionResponse extends RuntimeException {
    public ExceptionResponse(String message) {
        super(message);
    }
}