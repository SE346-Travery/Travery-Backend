package com.travery.traverybackend.dtos.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

// Không dùng @Data tránh lỗi để quy vô hạn nếu DTO chứa các nested object
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogoutRequest {
  @NotBlank(message = "Refresh token is required")
  private String refreshToken;
}
