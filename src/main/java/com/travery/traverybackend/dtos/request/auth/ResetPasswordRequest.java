package com.travery.traverybackend.dtos.request.auth;

import com.travery.traverybackend.validation.PasswordMatches;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

// Không dùng @Data tránh lỗi để quy vô hạn nếu DTO chứa các nested object
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches
@Builder
public class ResetPasswordRequest {
  @NotBlank(message = "Email is required")
  @Email(message = "Email is invalid")
  private String email;

  @NotBlank(message = "OTP is required")
  private String otp;

  @NotBlank(message = "New password is required")
  @Size(min = 8, message = "New password must be at least 8 characters")
  private String newPassword;

  @NotBlank(message = "Confirm password is required")
  private String confirmPassword;
}
