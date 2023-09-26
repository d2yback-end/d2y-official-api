package com.d2y.d2yapiofficial.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.d2y.d2yapiofficial.models.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {

  Optional<Token> findByToken(String token);

}
