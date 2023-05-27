package com.d2y.d2yofficialapi.utils;

import org.springframework.http.HttpStatus;

import com.d2y.d2yofficialapi.exceptions.CustomMessageException;

public class CustomMessageExceptionUtils {

  private CustomMessageExceptionUtils() {

  }

  public static CustomMessageException unauthorized() {
    CustomMessageException messageException = new CustomMessageException();
    messageException.setMessage("Unauthorized");
    messageException.setCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()));
    return messageException;
  }
}
