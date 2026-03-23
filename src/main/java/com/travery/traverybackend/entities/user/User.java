package com.travery.traverybackend.entities.user;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import com.travery.traverybackend.enums.AuthProvider;
import com.travery.traverybackend.enums.UserRoles;
import com.travery.traverybackend.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.redis.core.index.Indexed;

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

  @Column(name = "password_hashed", nullable = false)
  private String passwordHashed;

  @Indexed
  @Column(name = "email")
  private String email;

  @Column(name = "phone_number", unique = true)
  private String phoneNumber;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private UserStatus status = UserStatus.ACTIVE;

  @Column(name = "role")
  @Enumerated(EnumType.STRING)
  private UserRoles role;

  @Column(name = "comet_uid")
  private String cometUID;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AuthProvider authProvider;
}
