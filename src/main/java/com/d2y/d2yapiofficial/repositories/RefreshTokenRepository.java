package com.d2y.d2yapiofficial.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.d2y.d2yapiofficial.models.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  Optional<RefreshToken> findByToken(String token);

  void deleteByToken(String token);

}
