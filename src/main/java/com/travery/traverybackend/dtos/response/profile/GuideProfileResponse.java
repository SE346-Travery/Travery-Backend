package com.travery.traverybackend.dtos.response.profile;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GuideProfileResponse extends BaseUserProfileResponse {
  private String employeeCode;
  private String guideLicense;
  private List<String> languages;
  private int yearsExperience;
}
