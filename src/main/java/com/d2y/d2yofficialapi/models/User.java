package com.d2y.d2yofficialapi.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.FetchType;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Data it will error while fetch user from db
 *       remove @Data and add @Getter @Setter and no need to add @ToString
 */
@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_user")
public class User implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "username", unique = true, nullable = true)
  private String username;
  @Column(name = "email", unique = true, nullable = false)
  private String email;
  @Column(name = "password", nullable = false)
  private String password;
  @Column(name = "full_name")
  private String fullName;
  private int attempt;
  private String status;
  @Column(name = "created", updatable = false)
  private LocalDateTime created;
  @Column(name = "updated", insertable = false)
  private LocalDateTime updated;

  private boolean isEnabled = false;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
  private Collection<Role> roles;

  @OneToMany(mappedBy = "user")
  private List<Token> token;

}