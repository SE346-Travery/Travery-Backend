package com.travery.traverybackend.dtos.response.profile;

import com.travery.traverybackend.enums.user.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TouristProfileResponse extends BaseUserProfileResponse {
    private String passportNumber;
    private LocalDate dateOfBirth;
    private Gender gender;
}
