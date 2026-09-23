package io.vgrente.modelrouter.controller;

import io.vgrente.modelrouter.exception.NoModelAnswerException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/** Maps application exceptions to HTTP error responses. */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final String ERROR = "error";

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(NoModelAnswerException.class)
  public ResponseEntity<Map<String, Object>> handleNoModelAnswer(NoModelAnswerException ex) {
    return errorResponse(HttpStatus.BAD_GATEWAY, ex);
  }

  /** Returns a 400 response listing the fields that failed validation. */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(
      MethodArgumentNotValidException ex) {
    Map<String, String> fieldErrors = new HashMap<>();
    for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
      fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
    }
    Map<String, Object> body = new HashMap<>();

    body.put(ERROR, "Validation failed");
    body.put("details", fieldErrors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  /** Logs the exception and returns a generic 500 response without leaking its message. */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex) {
    logger.error("Unexpected error handling request", ex);
    Map<String, Object> body = new HashMap<>();
    body.put(ERROR, "An unexpected error occurred");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<Map<String, Object>> handleNotFound(NoResourceFoundException ex) {
    return errorResponse(HttpStatus.NOT_FOUND, ex);
  }

  private ResponseEntity<Map<String, Object>> errorResponse(HttpStatus status, Exception ex) {
    Map<String, Object> body = new HashMap<>();
    body.put(ERROR, ex.getMessage());
    return ResponseEntity.status(status).body(body);
  }
}
