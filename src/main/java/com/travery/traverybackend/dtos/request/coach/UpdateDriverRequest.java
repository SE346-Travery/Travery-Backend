package com.travery.traverybackend.dtos.request.coach;

import com.travery.traverybackend.enums.coach.DriverStatus;
import jakarta.validation.constraints.Size;
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
public class UpdateDriverRequest {

  @Size(max = 100, message = "Driver name must not exceed 100 characters")
  private String fullName;

  @Size(max = 20, message = "Phone number must not exceed 20 characters")
  private String phoneNumber;

  @Size(max = 50, message = "License number must not exceed 50 characters")
  private String licenseNumber;

  @Size(max = 500, message = "Avatar URL must not exceed 500 characters")
  private String avatarUrl;

  @Size(max = 255, message = "Avatar public ID must not exceed 255 characters")
  private String avatarPublicId;

  private DriverStatus status;
}
