package com.travery.traverybackend.dtos.response.staff;

import java.time.LocalDate;
import java.util.UUID;
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
public class ReceptionistBookingSummaryResponse {
  private UUID id;
  private String guestName;
  private String phoneNumber;
  private LocalDate checkInDate;
  private LocalDate checkOutDate;
  private String status;
}
