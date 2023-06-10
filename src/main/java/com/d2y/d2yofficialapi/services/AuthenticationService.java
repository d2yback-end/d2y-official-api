package com.d2y.d2yofficialapi.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.d2y.d2yofficialapi.constants.Constant;
import com.d2y.d2yofficialapi.dto.RegisterRequest;
import com.d2y.d2yofficialapi.dto.RegisterResponse;
import com.d2y.d2yofficialapi.dto.ResponseErrorTemplate;
import com.d2y.d2yofficialapi.exceptions.CustomMessageException;
import com.d2y.d2yofficialapi.interfaces.AuthenticationServiceInterface;
import com.d2y.d2yofficialapi.models.Role;
import com.d2y.d2yofficialapi.models.User;
import com.d2y.d2yofficialapi.repositories.RoleRepository;
import com.d2y.d2yofficialapi.repositories.UserRepository;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService implements AuthenticationServiceInterface {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public ResponseErrorTemplate registerUser(RegisterRequest request) {
    userRequestValidation(request);

    List<Role> roles = roleRepository.findAllByNameIn(request.roles());

    User user = User.builder()
        .id(0L)
        .username(request.username())
        .password(passwordEncoder.encode(request.password()))
        .fullName(request.fullName())
        .email(request.email())
        .roles(roles)
        .attempt(0)
        .status(Constant.ACT)
        .created(LocalDateTime.now())
        .build();

    userRepository.save(user);
    return userMapper(user);
  }

  @Override
  public void saveUserToken(User user, String jwtToken) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'saveUserToken'");
  }

  @Override
  public String validateToken(String theToken) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'validateToken'");
  }

  @Override
  public void revokeAllUserTokens(User user) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'revokeAllUserTokens'");
  }

  @Override
  public void refreshToken(HttpServletRequest request, HttpServletResponse response)
      throws IOException, StreamWriteException, DatabindException, java.io.IOException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'refreshToken'");
  }

  @Override
  public User getCurrentUser() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getCurrentUser'");
  }

  @Override
  public boolean isLoggedIn() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'isLoggedIn'");
  }

  public ResponseErrorTemplate userMapper(User user) {
    RegisterResponse registerResponse = new RegisterResponse(
        user.getUsername(),
        user.getEmail(),
        user.getFullName(),
        user.getRoles().stream().map(Role::getName).toList(),
        user.getCreated());
    return new ResponseErrorTemplate(Constant.SUC_MSG, Constant.SUC_CODE, registerResponse);
  }

  private void userRequestValidation(RegisterRequest userRequest) {

    // password must be not null or blank
    if (ObjectUtils.isEmpty(userRequest.password())) {
      throw new CustomMessageException("Password can't be blank or null",
          String.valueOf(HttpStatus.BAD_REQUEST));
    }

    // username and email must be not duplicate
    Optional<User> user = userRepository.findFirstByUsernameOrEmail(userRequest.username(), userRequest.email());
    if (user.isPresent()) {
      throw new CustomMessageException("Username or Email already exists.",
          String.valueOf(HttpStatus.BAD_REQUEST));
    }

    // check role valid request
    List<String> roles = roleRepository.findAll().stream().map(Role::getName).toList();
    for (var role : userRequest.roles()) {
      if (!roles.contains(role)) {
        throw new CustomMessageException("Role is invalid request.",
            String.valueOf(HttpStatus.BAD_REQUEST));
      }
    }
  }

}
