package com.travery.traverybackend.exception.error;

import com.travery.traverybackend.exception.AppErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CoachErrorCode implements AppErrorCode {
  COACH_NOT_FOUND("COACH_101", "Coach not found", HttpStatus.NOT_FOUND),
  DRIVER_NOT_FOUND("COACH_102", "Driver not found", HttpStatus.NOT_FOUND),
  ROUTE_NOT_FOUND("COACH_103", "Route not found", HttpStatus.NOT_FOUND),
  COACH_TRIP_NOT_FOUND("COACH_104", "Coach trip not found", HttpStatus.NOT_FOUND),
  COACH_TRIP_UPDATE_FAILED(
      "COACH_105",
      "Failed to update coach trip. Check pending or unchecked bookings.",
      HttpStatus.BAD_REQUEST);

  private final String errorCode;
  private final String message;
  private final HttpStatus httpStatus;
}
