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
@Builder
@PasswordMatches
public class RegisterRequest {
  @NotBlank(message = "Email is required")
  @Email(message = "Email is invalid")
  private String email;

  @NotBlank(message = "Password is required")
  @Size(min = 8, message = "Password must be at least 8 characters")
  private String password;

  @NotBlank(message = "Confirm password is required")
  private String confirmPassword;

  @NotBlank(message = "FullName is required")
  private String fullName;
}
