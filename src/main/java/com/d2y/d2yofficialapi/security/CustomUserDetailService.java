package com.d2y.d2yofficialapi.security;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.d2y.d2yofficialapi.constants.Constant;
import com.d2y.d2yofficialapi.exceptions.CustomMessageException;
import com.d2y.d2yofficialapi.models.User;
import com.d2y.d2yofficialapi.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return this.customUserDetail(username);
  }

  private CustomUserDetail customUserDetail(String username) {
    Optional<User> user = userRepository.findFirstByUsernameAndStatus(username, Constant.ACT);
    if (user.isEmpty()) {
      log.warn("Username {} unauthorized", username);
      throw new CustomMessageException("Unauthorized", String.valueOf(HttpStatus.UNAUTHORIZED.value()));
    }

    return new CustomUserDetail(
        user.get().getUsername(),
        user.get().getPassword(),
        user.get().getRoles()
            .stream().map(role -> new SimpleGrantedAuthority(role.getName()))
            .collect(Collectors.toList()),
        user.get().isEnabled());
  }

  public void saveUserAttemptAuthentication(String username) {
    Optional<User> user = userRepository.findFirstByUsernameAndStatus(username, Constant.ACT);
    if (user.isPresent()) {
      int attempt = user.get().getAttempt() + 1;
      user.get().setAttempt(attempt);
      user.get().setUpdated(LocalDateTime.now());
      if (user.get().getAttempt() > 3) {
        log.warn("User {} update status to blocked", username);
        user.get().setStatus(Constant.BLK);
      }
      userRepository.save(user.get());
    }
  }

  public void updateAttempt(String username) {
    Optional<User> user = userRepository.findFirstByUsernameAndStatus(username, Constant.ACT);
    if (user.isPresent()) {
      user.get().setAttempt(0);
      user.get().setUpdated(LocalDateTime.now());
      userRepository.save(user.get());
    }
  }

}
