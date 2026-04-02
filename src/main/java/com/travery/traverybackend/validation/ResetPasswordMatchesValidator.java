package com.travery.traverybackend.validation;

import com.travery.traverybackend.dtos.request.auth.ResetPasswordRequest;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ResetPasswordMatchesValidator
    implements ConstraintValidator<PasswordMatches, ResetPasswordRequest> {

  @Override
  public void initialize(PasswordMatches constraintAnnotation) {
    // Khởi tạo nếu cần (thường để trống)
  }

  @Override
  public boolean isValid(ResetPasswordRequest request, ConstraintValidatorContext context) {
    if (request.getNewPassword() == null || request.getConfirmPassword() == null) {
      return false;
    }

    boolean isValid = request.getNewPassword().equals(request.getConfirmPassword());

    if (!isValid) {
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
          .addPropertyNode("confirmPassword") // Gắn lỗi vào biến này
          .addConstraintViolation();
    }

    return isValid;
  }
}
