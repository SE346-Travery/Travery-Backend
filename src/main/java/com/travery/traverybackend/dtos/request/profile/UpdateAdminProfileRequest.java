package com.travery.traverybackend.dtos.request.profile;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAdminProfileRequest {
  private String fullName;

  @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number is invalid")
  private String phoneNumber;
}
