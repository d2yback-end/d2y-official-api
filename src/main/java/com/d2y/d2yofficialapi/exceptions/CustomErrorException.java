package com.d2y.d2yofficialapi.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.d2y.d2yofficialapi.dto.ResponseErrorTemplate;

public class CustomErrorException extends ResponseEntityExceptionHandler {

  @ExceptionHandler(CustomMessageException.class)
  public ResponseEntity<ResponseErrorTemplate> handleErrorException(CustomMessageException exception) {
    return ResponseEntity.ok(
        new ResponseErrorTemplate(
            exception.getMessage(), exception.getCode(),
            new Object()));
  }
}
