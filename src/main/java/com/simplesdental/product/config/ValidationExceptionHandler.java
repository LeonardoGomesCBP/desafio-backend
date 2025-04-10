package com.simplesdental.product.config;

import com.simplesdental.product.dto.ExceptionResponse;
import com.simplesdental.product.service.LoggingService;
import org.slf4j.Logger;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;


@ControllerAdvice
public class ValidationExceptionHandler {

    private static final Logger logger = LoggingService.getLogger(ValidationExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("error", "Internal Server Error");
        body.put("message", ex.getMessage());

        String path = request.getDescription(false).replace("uri=", "");
        logger.error("Unhandled exception: {} - path: {}", ex.getMessage(), path, ex);

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ExceptionResponse.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(ExceptionResponse ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("message", ex.getMessage());

        String path = request.getDescription(false).replace("uri=", "");
        body.put("path", path);

        logger.warn("Business exception: {} - path: {}", ex.getMessage(), path);

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        String path = request.getDescription(false).replace("uri=", "");

        body.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("path", path);

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        body.put("message", message);

        logger.warn("Validation error: {} - path: {}", message, path);

        return ResponseEntity.badRequest().body(body);
    }


    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("error", ex.getStatusCode().toString());
        body.put("message", ex.getReason());

        String path = request.getDescription(false).replace("uri=", "");

        if (ex.getStatusCode().is5xxServerError()) {
            logger.error("Server error: {} - path: {}", ex.getReason(), path);
        } else {
            logger.warn("Client error: {} - path: {}", ex.getReason(), path);
        }

        return new ResponseEntity<>(body, ex.getStatusCode());
    }


    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatchExceptions(TypeMismatchException ex, WebRequest request) {
        String message = String.format("Invalid value for parameter '%s'. Expected a numeric value.", ex.getPropertyName());
        String path = request.getDescription(false).replace("uri=", "");

        logger.warn("Type mismatch error: {} - path: {}", message, path);

        return ResponseEntity.badRequest().body(message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleTypeMismatchExceptions(MethodArgumentTypeMismatchException ex, WebRequest request) {
        String message = String.format("The parameter '%s' should have a value of type '%s'",
                ex.getName(),
                ex.getRequiredType().getSimpleName());

        String path = request.getDescription(false).replace("uri=", "");
        logger.warn("Method argument type mismatch: {} - path: {}", message, path);

        return ResponseEntity.badRequest().body(Collections.singletonMap("error", message));
    }
}