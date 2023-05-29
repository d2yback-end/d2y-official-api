package com.d2y.d2yofficialapi.filters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.d2y.d2yofficialapi.constants.Constant;
import com.d2y.d2yofficialapi.exceptions.CustomMessageException;
import com.d2y.d2yofficialapi.models.Role;
import com.d2y.d2yofficialapi.models.User;
import com.d2y.d2yofficialapi.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

  private final UserRepository userRepository;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    log.info("Authentication income {}", authentication);

    final String username = authentication.getName();
    final String password = authentication.getCredentials().toString();

    Optional<User> user = findUserByUsernameAndStatus(username);
    validateUserExistence(user);

    final List<GrantedAuthority> grantedAuthorities = grantedAuthorities(user.get().getRoles().stream().toList());
    final Authentication authenticatedUser = new UsernamePasswordAuthenticationToken(username, password,
        grantedAuthorities);

    log.info("Authentication out come {}", authenticatedUser);
    return authenticatedUser;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return false;
  }

  private Optional<User> findUserByUsernameAndStatus(String username) {
    try {
      return userRepository.findFirstByUsernameAndStatus(username, Constant.ACT);
    } catch (Exception ex) {
      log.error("{}", ex.getLocalizedMessage());
      throw new CustomMessageException("User not found.", String.valueOf(HttpStatus.UNAUTHORIZED.value()));
    }
  }

  private void validateUserExistence(Optional<User> user) {
    if (user.isEmpty()) {
      throw new CustomMessageException("User not found.", String.valueOf(HttpStatus.UNAUTHORIZED.value()));
    }
  }

  private List<GrantedAuthority> grantedAuthorities(List<Role> roles) {
    List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
    Set<String> permissions = new HashSet<>();

    if (!roles.isEmpty()) {
      roles.forEach(role -> {
        permissions.add(role.getName());
      });
    }

    permissions.forEach(permission -> grantedAuthorities.add(new SimpleGrantedAuthority(permission)));
    return grantedAuthorities;
  }

}
