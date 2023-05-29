package com.d2y.d2yofficialapi.filters;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.d2y.d2yofficialapi.helpers.JwtConfig;
import com.d2y.d2yofficialapi.services.JwtService;
import com.d2y.d2yofficialapi.utils.CustomMessageExceptionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationInternalFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final ObjectMapper objectMapper;
  private final JwtConfig jwtConfig;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    var accessToken = request.getHeader(jwtConfig.getHeader());
    log.info("Do filter uri {}", request.getRequestURI());

    if (accessToken != null && !accessToken.isBlank() && accessToken.startsWith(jwtConfig.getPrefix())) {
      accessToken = accessToken.substring((jwtConfig.getPrefix()).length());

      try {
        if (jwtService.isTokenValid(accessToken)) {
          Claims claims = jwtService.extractAllClaims(accessToken);
          var username = claims.getSubject();

          List<String> authorities = claims.get("authorities", List.class);
          if (username != null) {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                username, null,
                authorities.stream().map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList()));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
          }
        }
      } catch (Exception ex) {
        log.error("{}", ex.getLocalizedMessage());

        var messageException = CustomMessageExceptionUtils.unauthorized();
        var msgJson = objectMapper.writeValueAsString(messageException);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(msgJson);
        return;
      }
    }

    log.info("Do filter {}", request.getRequestURI());
    filterChain.doFilter(request, response);

  }

}
