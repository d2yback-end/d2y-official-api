package com.d2y.d2yofficialapi.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.d2y.d2yofficialapi.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findFirstByUsernameAndStatus(String username, String status);

  Optional<User> findFirstByUsernameOrEmail(String username, String email);

}
