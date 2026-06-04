package com.travery.traverybackend.entities.user;

import com.travery.traverybackend.entities.AbstractBaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
    name = "user_device_tokens",
    indexes = {
      @Index(name = "idx_user_device_tokens_phone_number", columnList = "phone_number"),
      @Index(name = "idx_user_device_tokens_fcm_token", columnList = "fcm_token")
    })
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserDeviceToken extends AbstractBaseEntity {

  @Column(name = "email", nullable = false)
  private String email;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "email", referencedColumnName = "email", insertable = false, updatable = false)
  private User user;


  @Column(name = "fcm_token", nullable = false, unique = true, columnDefinition = "TEXT")
  private String fcmToken;
}
