package com.travery.traverybackend.dtos.response.booking;

import com.travery.traverybackend.enums.booking.BookingStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class HotelBookingDetailResponse {
  private UUID id;
  private String hotelName;
  private String hotelAddress;
  private BigDecimal totalPrice;
  private LocalDateTime paymentDeadline;
  private BookingStatus status;
  private List<HotelBookingDetailItemResponse> items;
  private List<BookingMemberResponse> members;
  private PaymentInitiationResponse payment;
  private LocalDateTime createdAt;
}
