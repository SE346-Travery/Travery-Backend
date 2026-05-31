package com.travery.traverybackend.dtos.request.profile;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGuideProfileRequest {
  private String fullName;

  @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number is invalid")
  private String phoneNumber;

  private String guideLicense;

  @PositiveOrZero(message = "Years of experience must be zero or positive")
  private Integer yearsExperience;

  private List<String> languages;
}
