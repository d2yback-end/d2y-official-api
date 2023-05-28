package com.d2y.d2yofficialapi.interfaces;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.d2y.d2yofficialapi.security.CustomUserDetail;

import io.jsonwebtoken.Claims;

public interface JwtServiceInterface {

  String extractUsername(String token);

  List<String> extractRoles(CustomUserDetail customUserDetail);

  <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

  Claims extractAllClaims(String token);

  Key getSignInKey();

  String generateToken(CustomUserDetail customUserDetail);

  String generateToken(Map<String, Object> extraClaims, CustomUserDetail customUserDetail);

  String generateRefreshToken(CustomUserDetail customUserDetail);

  String buildToken(Map<String, Object> extraClaims, CustomUserDetail customUserDetail, long expiration);

  String createJwtToken(String username, Map<String, Object> extraClaims, List<String> roles, boolean isEnabled,
      long expiration);

  Date extractExpirationDate(String token);

  boolean isTokenValid(String token, CustomUserDetail customUserDetail);

  boolean isTokenExpired(String token);

}
