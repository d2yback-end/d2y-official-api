package com.d2y.d2yofficialapi.interfaces;

import com.d2y.d2yofficialapi.dto.RegisterRequest;
import com.d2y.d2yofficialapi.dto.ResponseErrorTemplate;
import com.d2y.d2yofficialapi.models.User;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationServiceInterface {

  ResponseErrorTemplate registerUser(RegisterRequest request);

  void saveUserToken(User user, String jwtToken);

  String validateToken(String theToken);

  void revokeAllUserTokens(User user);

  void refreshToken(HttpServletRequest request, HttpServletResponse response)
      throws IOException, StreamWriteException, DatabindException, java.io.IOException;

  User getCurrentUser();

  boolean isLoggedIn();

}
