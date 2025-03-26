package com.ludus.handlers;

import java.util.HashMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import com.ludus.exceptions.NotFoundException;
import com.ludus.exceptions.InvalidIdException;
import com.ludus.exceptions.ValidationException;
import java.util.Map;
import org.springframework.web.bind.annotation.ControllerAdvice;

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
    body.put("message", "Invalid ID format: must be a number");
    body.put("details", "The value '" + ex.getValue() + "' is not a valid ID");

    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<Map<String, Object>> handleNoHandlerFoundException(
      NoHandlerFoundException ex) {
    Map<String, Object> body = new HashMap<>();
    body.put("message", "The requested resource was not found: " + ex.getRequestURL());

    return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<Map<String, Object>> handleValidationException(ValidationException ex) {
    Map<String, Object> response = new HashMap<>();
    response.put("error", "Validation failed");
    response.put("details", ex.getErrors());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

}
