package com.travery.traverybackend.dtos.request.booking;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class CreateCoachBookingRequest {
  @NotNull(message = "Trip ID is required")
  private UUID tripId;

  @NotEmpty(message = "Must select at least one seat")
  private List<UUID> seatLayoutItemIds;

  @NotBlank(message = "Contact name is required")
  private String contactName;

  @NotBlank(message = "Contact phone is required")
  private String contactPhone;
}
