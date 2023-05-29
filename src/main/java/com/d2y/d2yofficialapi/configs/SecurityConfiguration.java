package com.d2y.d2yofficialapi.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.d2y.d2yofficialapi.exceptions.CustomAccessDeniedHandler;
import com.d2y.d2yofficialapi.filters.CustomAuthenticationProvider;
import com.d2y.d2yofficialapi.filters.JwtAuthenticationFilter;
import com.d2y.d2yofficialapi.filters.JwtAuthenticationInternalFilter;
import com.d2y.d2yofficialapi.helpers.JwtConfig;
import com.d2y.d2yofficialapi.security.CustomUserDetailService;
import com.d2y.d2yofficialapi.services.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration extends JwtConfig {

  private final CustomUserDetailService customUserDetailService;
  private final CustomAuthenticationProvider customAuthenticationProvider;
  private final JwtService jwtService;
  private final ObjectMapper objectMapper;
  private final JwtConfig jwtConfig;

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Autowired
  public void userAuthenticationGlobalConfig(AuthenticationManagerBuilder authenticationManagerBuilder) {
    authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

    AuthenticationManagerBuilder managerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
    managerBuilder.userDetailsService(customUserDetailService).passwordEncoder(passwordEncoder());
    AuthenticationManager authenticationManager = managerBuilder.build();

    httpSecurity
        .cors()
        .and()
        .csrf()
        .disable()
        .authorizeHttpRequests()
        .requestMatchers("/api/v1/auth/**").permitAll()
        .requestMatchers("/api/v1/admin/**").hasAuthority("ADMIN")
        .anyRequest().authenticated()
        .and()
        .authenticationManager(authenticationManager)
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .exceptionHandling()
        .authenticationEntryPoint(
            (((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED))))
        .accessDeniedHandler(new CustomAccessDeniedHandler())
        .and()
        .addFilterBefore(new JwtAuthenticationFilter(
            jwtService, objectMapper, jwtConfig, authenticationManager, customUserDetailService),
            UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(new JwtAuthenticationInternalFilter(jwtService, objectMapper, jwtConfig),
            UsernamePasswordAuthenticationFilter.class);

    return httpSecurity.build();
  }

}
