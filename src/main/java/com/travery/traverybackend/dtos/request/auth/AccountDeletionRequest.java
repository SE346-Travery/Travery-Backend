package com.travery.traverybackend.dtos.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDeletionRequest {
  @NotBlank(message = "Password is required to confirm account deletion")
  private String password;
}
