package com.travery.traverybackend.dtos.response.coach;

import com.travery.traverybackend.enums.booking.BookingStatus;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuideBookingResponse {
  private UUID bookingId;
  private String contactName;
  private String contactPhone;
  private int seatCount;
  private BookingStatus status;
  private List<String> seatNames;
}
