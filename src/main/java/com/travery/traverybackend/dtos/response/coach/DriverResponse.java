package com.travery.traverybackend.dtos.response.coach;

import com.travery.traverybackend.enums.coach.DriverStatus;
import java.util.UUID;
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
public class DriverResponse {
  private UUID id;
  private String fullName;
  private String phoneNumber;
  private String licenseNumber;
  private String avatarUrl;
  private String avatarPublicId;
  private DriverStatus status;
}
