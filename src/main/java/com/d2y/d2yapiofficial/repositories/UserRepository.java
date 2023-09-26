package com.d2y.d2yapiofficial.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.d2y.d2yapiofficial.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);
}