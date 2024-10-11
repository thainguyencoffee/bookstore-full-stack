package com.bookstorefullstack.bookstore.book.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NumberConstraintValidation.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NumberConstraint {

    String message() default "The number is not valid";

    Class<?> [] groups() default {};
    Class<? extends Payload> [] payload() default {};

    double min() default Double.MIN_VALUE;
    double max() default Double.MAX_VALUE;

}
