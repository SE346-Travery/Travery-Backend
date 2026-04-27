package com.travery.traverybackend.dtos.response.vehicle;

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
  private String phone;
  private String licenseNumber;
  private String status;
}
