package com.travery.traverybackend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MemberTypeMatchesValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMemberType {

  String message() default "Member type does not match date of birth";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
