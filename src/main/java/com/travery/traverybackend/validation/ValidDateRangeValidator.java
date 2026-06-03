package com.travery.traverybackend.validation;

import com.travery.traverybackend.dtos.request.booking.CreateHotelBookingRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ValidDateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {

  @Override
  public boolean isValid(Object obj, ConstraintValidatorContext context) {
    if (obj == null) {
      return true; // Let @NotNull handle this at the field level if needed
    }

    if (obj instanceof CreateHotelBookingRequest request) {
      LocalDate startDate = request.getStartDate();
      LocalDate endDate = request.getEndDate();

      if (startDate == null || endDate == null) {
        return true; // Let @NotNull handle nulls
      }

      boolean isValid = endDate.isAfter(startDate);

      if (!isValid) {
        context.disableDefaultConstraintViolation();
        context
            .buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
            .addPropertyNode("endDate") // Attach the error to the endDate field specifically
            .addConstraintViolation();
      }

      return isValid;
    }
    
    return true;
  }
}
