package com.travery.traverybackend.dtos.request.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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
public class CreateBookingRequest {
  @NotNull(message = "Tour instance ID is required")
  private UUID tourInstanceId;

  @NotBlank(message = "Passenger name is required")
  private String passengerName;

  @NotBlank(message = "Passenger phone is required")
  private String passengerPhone;

  @Positive(message = "Adult count must be positive")
  private int adultCount;

  @PositiveOrZero(message = "Child count must be zero or positive")
  private int childCount;

  private String specialNotes;

  @Valid
  private List<BookingMemberRequest> members;
}
