package com.travery.traverybackend.dtos.request.coach;

import com.travery.traverybackend.enums.coach.CoachType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
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
public class CreateSeatLayoutRequest {

  @NotBlank(message = "Layout name is required")
  @Size(max = 100, message = "Layout name must not exceed 100 characters")
  private String name;

  @NotNull(message = "Coach type is required")
  private CoachType coachType;

  @NotEmpty(message = "At least one seat item is required")
  @Valid
  private List<SeatLayoutItemRequest> items;
}
