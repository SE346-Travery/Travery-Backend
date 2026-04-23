package com.travery.traverybackend.dtos.request.auth;

import com.travery.traverybackend.enums.UserRoles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateStaffRequest {

  @NotBlank(message = "Email is required")
  @Email(message = "Email is invalid")
  private String email;

  @NotBlank(message = "Password is required")
  @Size(min = 8, message = "Password must be at least 8 characters")
  private String password;

  @NotBlank(message = "FullName is required")
  private String fullName;

  @NotNull(message = "Role is required")
  private UserRoles role;

  @Min(value = 0, message = "Experience year must be at least 0")
  private int experienceYear;
}
