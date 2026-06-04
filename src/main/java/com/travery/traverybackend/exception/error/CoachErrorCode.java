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
  INVALID_STATUS_TRANSITION(
      "COACH_105", "Invalid trip status transition", HttpStatus.valueOf(422)),
  TRIP_NOT_IN_PROGRESS(
      "COACH_106", "Action only allowed when trip is in progress", HttpStatus.CONFLICT);

  private final String errorCode;
  private final String message;
  private final HttpStatus httpStatus;
}
