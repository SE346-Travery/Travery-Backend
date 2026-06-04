package com.travery.traverybackend.dtos.response.booking;

import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.finance.PaymentMethod;
import com.travery.traverybackend.enums.finance.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
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
  private BigDecimal totalPrice;
  private LocalDateTime paymentDeadline;
  private BookingStatus status;
  private LocalDateTime createdAt;

  // Hotel info
  private String hotelName;
  private String hotelAddress;
  private LocalDate startDate;
  private LocalDate endDate;

  // Member & Room
  private List<BookingMemberResponse> members;
  private List<HotelBookingDetailItemResponse> items;

  // Payment info
  private PaymentMethod paymentMethod;
  private PaymentStatus paymentStatus;
  private UUID transactionId;
  private String gatewayTransactionId;
}
