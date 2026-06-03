package com.travery.traverybackend.dtos.response.staff;

import com.travery.traverybackend.enums.booking.MemberType;
import java.time.LocalDate;
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
public class HotelGuestResponse {
  private UUID id;
  private String fullName;
  private String identityNumber;
  private LocalDate dateOfBirth;
  private MemberType memberType;
}
