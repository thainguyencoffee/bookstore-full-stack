package com.bookstorefullstack.bookstore.book.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class VietnameseConstraintValidator implements ConstraintValidator<VietnamesePriceConstraint, Long> {

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return value % 1000L == 0;
    }
}
