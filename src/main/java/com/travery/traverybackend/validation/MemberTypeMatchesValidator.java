package com.travery.traverybackend.validation;

import com.travery.traverybackend.dtos.request.booking.BookingMemberRequest;
import com.travery.traverybackend.enums.booking.MemberType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Period;

public class MemberTypeMatchesValidator
    implements ConstraintValidator<ValidMemberType, BookingMemberRequest> {

  private static final int CHILD_AGE_THRESHOLD = 11;

  @Override
  public void initialize(ValidMemberType constraintAnnotation) {
    // No initialization needed
  }

  @Override
  public boolean isValid(BookingMemberRequest request, ConstraintValidatorContext context) {
    if (request.getDateOfBirth() == null || request.getMemberType() == null) {
      return true; // Let @NotNull handle null checks
    }

    int age = Period.between(request.getDateOfBirth(), LocalDate.now()).getYears();
    MemberType expected = age <= CHILD_AGE_THRESHOLD ? MemberType.CHILD : MemberType.ADULT;

    boolean isValid = request.getMemberType() == expected;

    if (!isValid) {
      context.disableDefaultConstraintViolation();
      String message =
          String.format(
              "Member type mismatch: declared %s but date of birth indicates %s (age %d)",
              request.getMemberType(), expected, age);
      context
          .buildConstraintViolationWithTemplate(message)
          .addPropertyNode("memberType")
          .addConstraintViolation();
    }

    return isValid;
  }
}
