package com.bookstore.resourceserver.book.validator;

import com.bookstore.resourceserver.book.impl.BookServiceImpl;
import com.bookstore.resourceserver.core.exception.CustomNoResultException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BookIsbnConstraintValidator implements ConstraintValidator<BookIsbnConstraint, String> {

    private final BookServiceImpl bookService;

    private String message;

    @Override
    public void initialize(BookIsbnConstraint constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String isbn, ConstraintValidatorContext context) {
        if (isbn == null || isbn.isBlank()) {
            return true; // Skip validation if book is null
        }

        if (isExistingBook(isbn)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

    private boolean isExistingBook(String isbn) {
        try {
            bookService.findByIsbn(isbn);
            return true;
        } catch (CustomNoResultException e) {
            return false;
        }
    }

}
