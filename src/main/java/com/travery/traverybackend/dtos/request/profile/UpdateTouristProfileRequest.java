package com.travery.traverybackend.dtos.request.profile;

import com.travery.traverybackend.enums.user.Gender;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTouristProfileRequest {
  private String fullName;

  @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number is invalid")
  private String phoneNumber;

  private String passportNumber;

  private LocalDate dateOfBirth;

  private Gender gender;
}
