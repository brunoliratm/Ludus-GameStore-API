package com.ludus.handlers;

import java.util.HashMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import com.ludus.exceptions.NotFoundException;
import com.ludus.exceptions.InvalidIdException;
import com.ludus.exceptions.ValidationException;
import com.ludus.exceptions.InvalidPageException;
import java.util.Map;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.http.converter.HttpMessageNotReadableException;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleGameNotFoundException(NotFoundException ex) {
    Map<String, Object> body = new HashMap<>();
    body.put("message", ex.getMessage());

    return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(InvalidIdException.class)
  public ResponseEntity<Map<String, Object>> handleInvalidIdException(InvalidIdException ex) {
    Map<String, Object> body = new HashMap<>();
    body.put("message", ex.getMessage());

    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<Map<String, Object>> handleTypeMismatch(
      MethodArgumentTypeMismatchException ex) {
    Map<String, Object> body = new HashMap<>();
    if ("page".equals(ex.getName())) {
        body.put("message", "Invalid Page format: must be a number");
    } else {
        body.put("message", "Invalid ID format: must be a number");
    }
    body.put("details", "The value '" + ex.getValue() + "' is not valid for parameter '" + ex.getName() + "'");

    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<Map<String, Object>> handleValidationException(ValidationException ex) {
    Map<String, Object> response = new HashMap<>();
    response.put("error", "Validation failed");
    response.put("details", ex.getErrors());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InvalidPageException.class)
  public ResponseEntity<Map<String, Object>> handleInvalidPageException(InvalidPageException ex) {
    Map<String, Object> body = new HashMap<>();
    body.put("message", ex.getMessage());

    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<Map<String, Object>> handleNoResourceFoundException(NoResourceFoundException ex) {
    Map<String, Object> body = new HashMap<>();
    body.put("message", "The requested route was not found.");
    return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
              
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
    Map<String, Object> body = new HashMap<>();
    
    body.put("details", "The provided JSON is malformed or contains syntax errors");
    
    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
    Map<String, Object> body = new HashMap<>();
    body.put("message", ex.getMessage());
    
    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }
}
