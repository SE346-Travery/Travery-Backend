package com.travery.traverybackend.dtos.response.booking;

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
public class BookingMemberResponse {
  private UUID id;
  private String fullName;
  private String passportNumber;
  private LocalDate dateOfBirth;
}
