package com.travery.traverybackend.exception.error;

import com.travery.traverybackend.exception.AppErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BookingErrorCode implements AppErrorCode {

  // === BOOKING CREATION ===
  TOUR_INSTANCE_NOT_FOUND("BOOKING_101", "Tour instance not found", HttpStatus.NOT_FOUND),
  TOUR_INSTANCE_NOT_OPEN(
      "BOOKING_102", "Tour instance is not open for booking", HttpStatus.CONFLICT),
  NOT_ENOUGH_SEATS(
      "BOOKING_103", "Not enough available seats for this tour instance", HttpStatus.CONFLICT),
  DEPARTURE_TOO_SOON(
      "BOOKING_104",
      "Booking requires at least 5 days before departure date",
      HttpStatus.BAD_REQUEST),
      
  COACH_TRIP_NOT_FOUND("BOOKING_105", "Coach trip not found", HttpStatus.NOT_FOUND),
  COACH_TRIP_NOT_OPEN("BOOKING_106", "Coach trip is not open for booking", HttpStatus.CONFLICT),
  SEAT_ALREADY_BOOKED("BOOKING_107", "One or more requested seats are already booked", HttpStatus.CONFLICT),
  INVALID_SEAT_LAYOUT("BOOKING_108", "One or more requested seats are invalid for this coach", HttpStatus.BAD_REQUEST),

  // === BOOKING ACCESS ===
  BOOKING_NOT_FOUND("BOOKING_201", "Booking not found", HttpStatus.NOT_FOUND),
  BOOKING_ACCESS_DENIED(
      "BOOKING_202", "You do not have access to this booking", HttpStatus.FORBIDDEN),

  // === PAYMENT ===
  BOOKING_NOT_PENDING("BOOKING_301", "Booking is not in PENDING status", HttpStatus.CONFLICT),
  PAYMENT_DEADLINE_EXPIRED("BOOKING_302", "Payment deadline has expired", HttpStatus.CONFLICT),
  BOOKING_NOT_PAID("BOOKING_303", "Booking must be in PAID status", HttpStatus.CONFLICT),

  // === CANCELLATION ===
  BOOKING_ALREADY_CANCELLED("BOOKING_401", "Booking is already cancelled", HttpStatus.CONFLICT),
  CANNOT_CANCEL_IN_PROGRESS(
      "BOOKING_402", "Cannot cancel booking for an in-progress tour", HttpStatus.CONFLICT),
  CANNOT_CANCEL_COMPLETED(
      "BOOKING_403", "Cannot cancel booking for a completed tour", HttpStatus.CONFLICT),

  // === REVIEW ===
  REVIEW_BOOKING_NOT_PAID(
      "BOOKING_501", "Cannot review: booking is not paid", HttpStatus.BAD_REQUEST),
  REVIEW_TOUR_NOT_COMPLETED(
      "BOOKING_502", "Cannot review: tour has not been completed", HttpStatus.BAD_REQUEST),
  REVIEW_ALREADY_EXISTS(
      "BOOKING_503", "A review already exists for this booking", HttpStatus.CONFLICT);

  private final String errorCode;
  private final String message;
  private final HttpStatus httpStatus;
}
