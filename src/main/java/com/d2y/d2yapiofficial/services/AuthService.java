package com.d2y.d2yapiofficial.services;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.d2y.d2yapiofficial.dto.AuthResponse;
import com.d2y.d2yapiofficial.dto.LoginRequest;
import com.d2y.d2yapiofficial.dto.RefreshTokenRequest;
import com.d2y.d2yapiofficial.dto.RegisterRequest;
import com.d2y.d2yapiofficial.models.NotificationEmail;
import com.d2y.d2yapiofficial.models.Token;
import com.d2y.d2yapiofficial.models.User;
import com.d2y.d2yapiofficial.repositories.TokenRepository;
import com.d2y.d2yapiofficial.repositories.UserRepository;
import com.d2y.d2yapiofficial.security.JwtProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthService {

  private final MailService mailService;
  private final TokenRepository tokenRepository;
  private final UserRepository userRepository;
  private final RefreshTokenService refreshTokenService;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtProvider jwtProvider;

  @Transactional
  public void registerUser(RegisterRequest registrationDto) {
    try {
      if (userRepository.existsByEmail(registrationDto.getEmail())) {
        throw new EntityExistsException("Email already exists!");
      }

      if (registrationDto.getPassword().length() < 8) {
        throw new ValidationException("Password must be at least 8 characters long.");
      }

      if (!isStrongPassword(registrationDto.getPassword())) {
        throw new ValidationException(
            "Password must contain a combination of uppercase letters, lowercase letters, numbers, and special characters.");
      }

      User user = new User();
      user.setUsername(registrationDto.getUsername());
      user.setEmail(registrationDto.getEmail());
      user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
      user.setCreatedOn(new Timestamp(System.currentTimeMillis()));
      user.setLastModifiedOn(new Timestamp(System.currentTimeMillis()));
      user.setActive(true);
      user.setEnabled(false);

      user = userRepository.save(user);

      sendVerificationEmail(user);
    } catch (Exception ex) {
      log.info(ex.getMessage());
      ex.printStackTrace();
      throw ex;
    }
  }

  @Transactional
  public AuthResponse login(LoginRequest loginRequest) {
    try {
      Authentication authenticate = authenticationManager
          .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
              loginRequest.getPassword()));

      SecurityContextHolder.getContext().setAuthentication(authenticate);
      String token = jwtProvider.generateToken(authenticate);

      return AuthResponse.builder()
          .accessToken(token)
          .refreshToken(refreshTokenService.generateRefreshToken().getToken())
          .build();
    } catch (Exception ex) {
      log.info(ex.getMessage());
      ex.printStackTrace();
      throw ex;
    }
  }

  public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
    refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
    String token = jwtProvider.generateTokenWithUserName(refreshTokenRequest.getEmail());

    return AuthResponse.builder()
        .accessToken(token)
        .refreshToken(refreshTokenService.generateRefreshToken().getToken())
        .build();
  }

  private boolean isStrongPassword(String password) {
    return password.matches("^(?=.*[a-zA-Z0-9!@#$%^&*()_+=\\-\\[\\]{}\\/?.,><\\\\|]).+$");
  }

  private void sendVerificationEmail(User user) {
    NotificationEmail mailMessage = createNotificationEmail(user);
    mailService.sendMail(mailMessage);
  }

  private NotificationEmail createNotificationEmail(User user) {
    String recipientEmail = user.getEmail();
    String subject = "Welcome " + user.getUsername();
    String token = generateVerificationToken(user);
    String verificationUrl = "http://localhost:5000/api/v1/auth/accountVerification/" + token;

    NotificationEmail notificationEmail = new NotificationEmail();
    notificationEmail.setRecipient(recipientEmail);
    notificationEmail.setSubject(subject);
    notificationEmail.setUsername(user.getUsername());
    notificationEmail.setVerificationUrl(verificationUrl);

    return notificationEmail;
  }

  private String generateVerificationToken(User user) {
    String token = UUID.randomUUID().toString();
    Token verificationToken = new Token();
    verificationToken.setToken(token);
    verificationToken.setUser(user);

    tokenRepository.save(verificationToken);
    return token;
  }

  public void verifyAccount(String token) {
    Optional<Token> verificationToken = tokenRepository.findByToken(token);
    fetchUserAndEnable(verificationToken.orElseThrow(() -> new ValidationException("Invalid Verification Token")));
  }

  private void fetchUserAndEnable(Token verificationToken) {
    Long userId = verificationToken.getUser().getUserId();
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    user.setEnabled(true);
    userRepository.save(user);
  }

  @Transactional()
  public User getCurrentUser() {
    Jwt principal = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return userRepository.findByEmail(principal.getSubject())
        .orElseThrow(() -> new EntityNotFoundException("User Not Found"));
  }
}
