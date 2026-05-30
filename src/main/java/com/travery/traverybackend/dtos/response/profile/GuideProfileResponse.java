package com.travery.traverybackend.dtos.response.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

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
