package com.travery.traverybackend.dtos.request.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAmenityRequest {
  @NotBlank private String name;
  @NotBlank private String type;
  private String iconUrl;
}
