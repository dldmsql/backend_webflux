package com.example.backend_webflux.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

  private final ApiExceptionEnum error;

  public ApiException(ApiExceptionEnum e) {
    super(e.getMessage());
    this.error = e;
  }
}
