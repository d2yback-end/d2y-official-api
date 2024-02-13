package com.d2y.d2yapiofficial.dto.user;

import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonApiTypeForClass("User")
public class UserDTO {
  @Id
  private Long userId;

  @NotBlank(message = "Username cannot be empty")
  @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters")
  private String username;

  @NotBlank(message = "Password cannot be empty")
  @Size(min = 6, message = "Password must be at least 6 characters long")
  private String password;

  @NotBlank(message = "Email cannot be empty")
  @Email(message = "Invalid email format")
  private String email;

  private String photoProfile;

  @JsonProperty("isActive")
  private boolean active;
}
