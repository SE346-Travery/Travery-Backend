package com.travery.traverybackend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidDateRangeValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateRange {

  String message() default "End date must be strictly after start date";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
