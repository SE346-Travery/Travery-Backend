package com.travery.traverybackend.dtos.response.profile;

import com.travery.traverybackend.enums.user.UserRoles;
import com.travery.traverybackend.enums.user.UserStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseUserProfileResponse {
  private UUID id;
  private String fullName;
  private String email;
  private String phoneNumber;
  private String avatarUrl;
  private UserStatus status;
  private UserRoles role;
  private LocalDateTime createdAt;
}
