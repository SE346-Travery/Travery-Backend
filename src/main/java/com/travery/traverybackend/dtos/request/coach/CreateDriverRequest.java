package com.travery.traverybackend.dtos.request.coach;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDriverRequest {
  @NotBlank(message = "Full name is required")
  private String fullName;

  @NotBlank(message = "Phone number is required")
  private String phoneNumber;

  @NotBlank(message = "License number is required")
  private String licenseNumber;
}
