package com.bookstore.resourceserver.book.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = VietnameseConstraintValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface VietnamesePriceConstraint {
    String message() default "Vietnamese price is invalid.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
