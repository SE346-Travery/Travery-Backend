package com.travery.traverybackend.dtos.request.tour;

import com.travery.traverybackend.enums.tour.TourInstanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TourProgressUpdateRequest {
  @NotNull(message = "Status is required")
  private TourInstanceStatus status;
}
