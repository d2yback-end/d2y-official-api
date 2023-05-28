package com.d2y.d2yofficialapi.services;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Service;

import com.d2y.d2yofficialapi.exceptions.CustomMessageException;
import com.d2y.d2yofficialapi.helpers.JwtConfig;
import com.d2y.d2yofficialapi.interfaces.JwtServiceInterface;
import com.d2y.d2yofficialapi.security.CustomUserDetail;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService extends JwtConfig implements JwtServiceInterface {

  @Override
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  @Override
  public List<String> extractRoles(CustomUserDetail customUserDetail) {
    return customUserDetail.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());
  }

  /**
   * Dalam JWT, (claims) adalah informasi yang terkandung dalam token
   * yang mewakili sejumlah pernyataan tentang entitas yang diberi token
   */
  @Override
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  @Override
  public Claims extractAllClaims(String token) {
    try {
      return Jwts
          .parserBuilder()
          .setSigningKey(getSignInKey())
          .build()
          .parseClaimsJws(token)
          .getBody();

    } catch (ExpiredJwtException exception) {
      log.error(exception.getLocalizedMessage());
      throw new CustomMessageException("Token expiration", String.valueOf(HttpStatus.UNAUTHORIZED.value()));
    } catch (UnsupportedJwtException exception) {
      log.error(exception.getLocalizedMessage());
      throw new CustomMessageException("Token is not support.", String.valueOf(HttpStatus.UNAUTHORIZED.value()));
    } catch (MalformedJwtException | SignatureException exception) {
      log.error(exception.getLocalizedMessage());
      throw new CustomMessageException("Token is invalid format.", String.valueOf(HttpStatus.UNAUTHORIZED.value()));
    } catch (Exception exception) {
      log.error(exception.getLocalizedMessage());
      throw new CustomMessageException(exception.getLocalizedMessage(),
          String.valueOf(HttpStatus.UNAUTHORIZED.value()));
    }
  }

  @Override
  public Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(getSecretKey());
    return Keys.hmacShaKeyFor(keyBytes);
  }

  @Override
  public String generateToken(CustomUserDetail customUserDetail) {
    return generateToken(new HashMap<>(), customUserDetail);
  }

  @Override
  public String generateToken(Map<String, Object> extraClaims, CustomUserDetail customUserDetail) {
    return buildToken(extraClaims, customUserDetail, getExpiration());
  }

  @Override
  public String generateRefreshToken(CustomUserDetail customUserDetail) {
    return buildToken(new HashMap<>(), customUserDetail, getRefreshExpiration());
  }

  @Override
  public String buildToken(Map<String, Object> extraClaims, CustomUserDetail customUserDetail, long expiration) {
    List<String> roles = extractRoles(customUserDetail);

    return createJwtToken(customUserDetail.getUsername(), extraClaims, roles, customUserDetail.isEnabled(), expiration);
  }

  @Override
  public String createJwtToken(String username, Map<String, Object> extraClaims, List<String> roles, boolean isEnabled,
      long expiration) {
    JwtBuilder jwtBuilder = Jwts.builder();

    jwtBuilder.setSubject(username);
    jwtBuilder.setClaims(extraClaims);
    jwtBuilder.claim("authorities", roles);
    jwtBuilder.claim("isEnabled", isEnabled);
    jwtBuilder.setIssuedAt(new Date(System.currentTimeMillis()));
    jwtBuilder.setExpiration(new Date(System.currentTimeMillis() + expiration));
    jwtBuilder.signWith(getSignInKey(), SignatureAlgorithm.HS256);

    return jwtBuilder.compact();
  }

  @Override
  public Date extractExpirationDate(String token) {
    return extractClaim(token, Claims::getExpiration);

  }

  @Override
  public boolean isTokenValid(String token, CustomUserDetail customUserDetail) {
    final String username = extractUsername(token);
    return (username.equals(customUserDetail.getUsername())) && !isTokenExpired(token);
  }

  @Override
  public boolean isTokenExpired(String token) {
    return extractExpirationDate(token).before(new Date());
  }

}
