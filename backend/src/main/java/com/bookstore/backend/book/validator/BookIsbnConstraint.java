package com.bookstore.backend.book.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = BookIsbnConstraintValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface BookIsbnConstraint {

    String message() default "The isbn of the book is already in use";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
