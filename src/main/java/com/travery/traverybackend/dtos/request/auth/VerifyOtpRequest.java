package com.travery.traverybackend.dtos.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

// Không dùng @Data tránh lỗi để quy vô hạn nếu DTO chứa các nested object
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyOtpRequest {
  @NotBlank(message = "Email is required")
  @Email(message = "Email is invalid")
  private String email;

  @NotBlank(message = "OTP is required")
  @Pattern(regexp = "^[0-9]{6}$", message = "OTP must be 6 digits")
  private String otp;
}
