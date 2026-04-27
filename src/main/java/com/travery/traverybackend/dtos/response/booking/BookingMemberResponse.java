package com.travery.traverybackend.dtos.response.booking;

import com.travery.traverybackend.enums.IdentityType;
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
public class BookingMemberResponse {
  private UUID id;
  private String fullName;
  private String identityNumber;
  private IdentityType identityType;
  private boolean isChild;
  private String status;
}
