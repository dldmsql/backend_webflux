package com.example.backend_webflux.exception;

import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiExceptionAdvice {

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ExceptionResponse> exceptionHandler(ApiException e) {
    ApiExceptionEnum exception = e.getError();
    log.error(exception.getHttpStatus().getReasonPhrase(), e);

    return ResponseEntity.status(exception.getStatus()).body(
        ExceptionResponse.builder().status(exception.getStatus()).message(exception.getMessage())
            .build());
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ExceptionResponse> validationExceptionHandler(
      ConstraintViolationException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        ExceptionResponse.builder().status(HttpStatus.BAD_REQUEST.value()).message(e.getMessage())
            .build());
  }

  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<ExceptionResponse> notFoundExceptionHandler(
      ConstraintViolationException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
        ExceptionResponse.builder().status(HttpStatus.NOT_FOUND.value()).message(e.getMessage())
            .build());
  }
}
