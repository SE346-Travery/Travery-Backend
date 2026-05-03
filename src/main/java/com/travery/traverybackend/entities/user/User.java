package com.travery.traverybackend.entities.user;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.enums.auth.AuthProvider;
import com.travery.traverybackend.enums.user.UserRoles;
import com.travery.traverybackend.enums.user.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends AbstractBaseEntity {
  @Column(name = "full_name", nullable = false)
  private String fullName;

  @Column(name = "password_hashed")
  private String passwordHashed;

  @Column(name = "email", unique = true)
  private String email;

  @Column(name = "phone_number", unique = true)
  private String phoneNumber;

  @Column(name = "avatar_url", length = 500)
  private String avatarUrl;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  @Builder.Default
  private UserStatus status = UserStatus.PENDING;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  private UserRoles role;

  @Column(name = "cometchat_uid", unique = true, length = 100)
  private String cometchatUID;

  @Enumerated(EnumType.STRING)
  @Column(name = "auth_provider", nullable = false, length = 20)
  private AuthProvider authProvider;
}
