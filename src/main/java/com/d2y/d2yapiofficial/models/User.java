package com.d2y.d2yapiofficial.models;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long userId;

  @Column(name = "username")
  private String username;

  @Column(name = "password")
  private String password;

  @Column(name = "email")
  private String email;

  @Column(name = "photo_profile")
  private String photoProfile;

  @Column(name = "created_on")
  private Timestamp createdOn;

  @Column(name = "last_modified_on")
  private Timestamp lastModifiedOn;

  @Column(name = "is_active")
  private boolean active;

  @Column(name = "is_enable")
  private boolean enabled;

}
