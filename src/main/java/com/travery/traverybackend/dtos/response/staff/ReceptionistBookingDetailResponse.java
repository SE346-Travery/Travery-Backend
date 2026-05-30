package com.travery.traverybackend.dtos.response.staff;

import com.travery.traverybackend.dtos.response.booking.AddOnOrderResponse;
import com.travery.traverybackend.dtos.response.booking.BookingMemberResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
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
public class ReceptionistBookingDetailResponse {
  private UUID id;
  private String guestName;
  private String phoneNumber;
  private LocalDate checkInDate;
  private LocalDate checkOutDate;
  private String status;
  private BigDecimal totalPrice;
  private List<BookingMemberResponse> manifest;
  private List<RoomAllocationResponse> roomAllocations;
  private List<AddOnOrderResponse> addOnOrders;
}
