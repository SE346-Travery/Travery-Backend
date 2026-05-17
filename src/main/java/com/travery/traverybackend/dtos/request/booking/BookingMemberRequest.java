package com.travery.traverybackend.dtos.request.booking;

import com.travery.traverybackend.enums.booking.MemberType;
import com.travery.traverybackend.validation.ValidMemberType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
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
@ValidMemberType
public class BookingMemberRequest {

  @NotBlank(message = "Full name is required")
  private String fullName;

  @NotBlank(message = "Identity number is required")
  private String identityNumber;

  @NotNull(message = "Date of birth is required")
  @Past(message = "Date of birth must be in the past")
  private LocalDate dateOfBirth;

  @NotNull(message = "Member type is required (ADULT or CHILD)")
  private MemberType memberType;
}
