package com.travery.traverybackend.dtos.request.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
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
public class CreateHotelBookingRequest {
  private UUID tourInstanceId; // Optional, if booked as part of a tour

  @NotEmpty @Valid private List<HotelBookingRequestDetail> rooms;

  @NotEmpty @Valid private List<BookingMemberRequest> members;

  private String ipAddress; // Set by controller
}
