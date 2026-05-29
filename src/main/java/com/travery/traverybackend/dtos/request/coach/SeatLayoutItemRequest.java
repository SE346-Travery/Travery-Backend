package com.travery.traverybackend.dtos.request.coach;

import com.travery.traverybackend.enums.coach.SeatPosition;
import com.travery.traverybackend.enums.coach.SeatTier;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class SeatLayoutItemRequest {

  @NotBlank(message = "Seat name is required")
  @Size(max = 10, message = "Seat name must not exceed 10 characters")
  private String seatName;

  @NotNull(message = "Tier is required")
  private SeatTier tier;

  @NotNull(message = "Position is required")
  private SeatPosition position;

  @Min(value = 0, message = "Row number must be non-negative")
  private int rowNumber;

  @Min(value = 0, message = "Column number must be non-negative")
  private int columnNumber;
}
