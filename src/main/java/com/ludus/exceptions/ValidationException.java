package com.ludus.exceptions;

import java.util.List;

public class ValidationException extends RuntimeException {
    private final List<String> errors;

    public ValidationException(List<String> errors) {
        super("Validation error(s)");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }

    public ValidationException(String message) {
        super(message);
        this.errors = List.of(); 
    }
}
