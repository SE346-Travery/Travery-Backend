package com.travery.traverybackend.dtos.request.coach;

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
  private String fullName;
  private String phoneNumber;
  private String licenseNumber;
  private String status;
}
