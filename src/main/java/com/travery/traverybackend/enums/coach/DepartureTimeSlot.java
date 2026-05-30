package com.travery.traverybackend.enums.coach;

import java.time.LocalTime;
import lombok.Getter;

@Getter
public enum DepartureTimeSlot {
  EARLY_MORNING(LocalTime.of(4, 0), LocalTime.of(7, 0)),
  MORNING(LocalTime.of(7, 0), LocalTime.of(12, 0)),
  AFTERNOON(LocalTime.of(12, 0), LocalTime.of(17, 0)),
  EVENING(LocalTime.of(17, 0), LocalTime.MAX);

  private final LocalTime startTime;
  private final LocalTime endTime;

  DepartureTimeSlot(LocalTime startTime, LocalTime endTime) {
    this.startTime = startTime;
    this.endTime = endTime;
  }
}
