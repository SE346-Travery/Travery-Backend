package com.travery.traverybackend.dtos.response.tour;

import java.time.LocalDate;
import java.util.UUID;

import com.travery.traverybackend.enums.tour.TourInstanceStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TourInstanceResponse {
  private UUID id;
  private LocalDate startDate;
  private LocalDate endDate;
  private TourInstanceStatus status;
  private Integer availableSlots;
}
