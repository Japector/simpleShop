package org.japector.shopping.exception;

import org.springframework.http.ResponseEntity;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}