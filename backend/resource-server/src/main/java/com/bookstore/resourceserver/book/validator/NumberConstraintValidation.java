package com.bookstore.resourceserver.book.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NumberConstraintValidation implements ConstraintValidator<NumberConstraint, Number> {

    private double min;
    private double max;
    private String message;

    @Override
    public void initialize(NumberConstraint constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
        this.message = constraintAnnotation.message();
    }

    @Override
        public boolean isValid(Number number, ConstraintValidatorContext context) {
        if (number == null) {
            return true;
        }

        double doubleValue = number.doubleValue();
        boolean isInValid = doubleValue < min || doubleValue > max;

        if (isInValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
