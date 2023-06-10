package com.d2y.d2yofficialapi.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.d2y.d2yofficialapi.dto.RegisterRequest;
import com.d2y.d2yofficialapi.services.AuthenticationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService authenticationService;

  @PostMapping("/register")
  public ResponseEntity<Object> register(@RequestBody RegisterRequest userRequest) {
    log.info("Intercept registration new user with req: {}", userRequest);
    return ResponseEntity.ok(authenticationService.registerUser(userRequest));
  }
}
