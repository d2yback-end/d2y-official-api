package com.d2y.d2yofficialapi.filters;

import java.io.IOException;
import java.util.Collections;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.d2y.d2yofficialapi.dto.AuthenticationRequest;
import com.d2y.d2yofficialapi.dto.AuthenticationResponse;
import com.d2y.d2yofficialapi.helpers.JwtConfig;
import com.d2y.d2yofficialapi.security.CustomUserDetail;
import com.d2y.d2yofficialapi.security.CustomUserDetailService;
import com.d2y.d2yofficialapi.services.JwtService;
import com.d2y.d2yofficialapi.utils.CustomMessageExceptionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

  private final JwtService jwtService;
  private final ObjectMapper objectMapper;
  private final CustomUserDetailService customUserDetailService;

  public JwtAuthenticationFilter(
      JwtService jwtService,
      ObjectMapper objectMapper,
      JwtConfig jwtConfig,
      AuthenticationManager authenticationManager,
      CustomUserDetailService customUserDetailService) {
    super(new AntPathRequestMatcher(jwtConfig.getUrl(), "POST"));
    setAuthenticationManager(authenticationManager);
    this.jwtService = jwtService;
    this.objectMapper = objectMapper;
    this.customUserDetailService = customUserDetailService;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException, IOException, ServletException {

    log.info("Start attempt to authentication");
    AuthenticationRequest authenticationRequest = objectMapper.readValue(request.getInputStream(),
        AuthenticationRequest.class);

    customUserDetailService.saveUserAttemptAuthentication(authenticationRequest.username());
    log.info("End attempt to authentication");

    return getAuthenticationManager()
        .authenticate(new UsernamePasswordAuthenticationToken(
            authenticationRequest.username(),
            authenticationRequest.password(),
            Collections.emptyList()));
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
      Authentication authResult) throws IOException, ServletException {

    CustomUserDetail customUserDetail = (CustomUserDetail) authResult.getPrincipal();
    var accessToken = jwtService.generateToken(customUserDetail);
    var refreshToken = jwtService.generateRefreshToken(customUserDetail);
    customUserDetailService.updateAttempt(customUserDetail.getUsername());

    AuthenticationResponse authenticationResponse = new AuthenticationResponse(
        accessToken,
        refreshToken);

    var jsonUser = objectMapper.writeValueAsString(authenticationResponse);

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write(jsonUser);
    log.info("Successful Authentication {}", authenticationResponse);
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException failed) throws IOException, ServletException {

    var messageException = CustomMessageExceptionUtils.unauthorized();
    var msgJson = objectMapper.writeValueAsString(messageException);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write(msgJson);
    log.info("Unsuccessful Authentication {}", failed.getLocalizedMessage());
  }

}
