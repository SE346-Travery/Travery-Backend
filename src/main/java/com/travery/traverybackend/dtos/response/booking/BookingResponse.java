package com.travery.traverybackend.dtos.response.booking;

import java.math.BigDecimal;
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
public class BookingResponse {
  private UUID id;
  private UUID userId;
  private UUID tourInstanceId;
  private String passengerName;
  private String passengerPhone;
  private int adultCount;
  private int childCount;
  private BigDecimal totalPrice;
  private String status;
  private List<BookingMemberResponse> members;
}
