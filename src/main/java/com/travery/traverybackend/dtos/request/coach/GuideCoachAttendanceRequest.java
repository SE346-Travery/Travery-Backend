package com.travery.traverybackend.dtos.request.coach;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class GuideCoachAttendanceRequest {
  @NotNull(message = "Booking IDs list cannot be null")
  @NotEmpty(message = "Booking IDs list cannot be empty")
  private List<UUID> bookingIds;
}
