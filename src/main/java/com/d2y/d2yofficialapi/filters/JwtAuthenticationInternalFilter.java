package com.d2y.d2yofficialapi.filters;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.d2y.d2yofficialapi.helpers.JwtConfig;
import com.d2y.d2yofficialapi.repositories.TokenRepository;
import com.d2y.d2yofficialapi.security.CustomUserDetailService;
import com.d2y.d2yofficialapi.services.JwtService;
import com.d2y.d2yofficialapi.utils.CustomMessageExceptionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

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
  private final CustomUserDetailService userDetailsService;
  private final TokenRepository tokenRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    if (request.getServletPath().contains(jwtConfig.getUrl())) {
      filterChain.doFilter(request, response);
      return;
    }

    final String authHeader = request.getHeader(jwtConfig.getHeader());
    final String jwt;
    final String userEmail;

    if (authHeader == null || !authHeader.startsWith(jwtConfig.getPrefix())) {
      filterChain.doFilter(request, response);
      return;
    }

    jwt = authHeader.split(" ")[1].trim();
    userEmail = jwtService.extractUsername(jwt);

    try {
      if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
        var isTokenValid = tokenRepository.findByToken(jwt)
            .map(token -> !token.isExpired() && !token.isRevoked())
            .orElse(false);

        if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
          UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
              userDetails,
              null,
              userDetails.getAuthorities());

          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      }
    } catch (Exception exception) {
      log.error("{}", exception.getLocalizedMessage());

      var messageException = CustomMessageExceptionUtils.unauthorized();
      var msgJson = objectMapper.writeValueAsString(messageException);

      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.getWriter().write(msgJson);
      return;
    }
  }

}
