package com.travery.traverybackend.dtos.request.booking;

import com.travery.traverybackend.enums.IdentityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class BookingMemberRequest {
  @NotBlank(message = "Full name is required")
  private String fullName;

  private String identityNumber;

  @NotNull(message = "Identity type is required")
  private IdentityType identityType;

  private boolean isChild;
}
