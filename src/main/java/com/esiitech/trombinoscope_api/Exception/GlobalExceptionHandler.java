package com.esiitech.trombinoscope_api.Exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(ApiException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", true);
        response.put("message", ex.getMessage());
        response.put("status", ex.getStatus().value());

        return new ResponseEntity<>(response, ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", true);
        response.put("message", "Une erreur inattendue est survenue.");
        response.put("status", 500);

        return ResponseEntity.internalServerError().body(response);
    }
}

