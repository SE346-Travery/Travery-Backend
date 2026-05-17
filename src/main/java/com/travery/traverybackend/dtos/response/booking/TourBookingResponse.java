package com.travery.traverybackend.dtos.response.booking;

import com.travery.traverybackend.enums.booking.BookingStatus;
import java.util.List;
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
public class TourBookingResponse {
  private UUID id;
  private String customerName;
  private String customerPhone;
  private String specialRequests;
  private BookingStatus status;
  private List<BookingMemberResponse> members;
}
