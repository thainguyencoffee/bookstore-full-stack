package com.bookstore.backend.book;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class BookValidator implements Validator {

    private final BookRepository bookRepository;

    public BookValidator(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * @param clazz
     * @return
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return Book.class.equals(clazz);
    }

    /**
     * @param target
     * @param errors
     */
    @Override
    public void validate(Object target, Errors errors) {
        Book book = (Book) target;
        Set<ConstraintViolation<Object>> violations =
                Validation.buildDefaultValidatorFactory().getValidator().validate(book);
        violations.forEach(violation -> {
            String property = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.rejectValue(property, "", message);
        });

        if (book.getIsbn() != null && isExistingBook(book.getIsbn(), book.getId())) {
            errors.rejectValue("isbn", "", "The isbn of the book is already in use");
        }
    }

    private boolean isExistingBook(String isbn, Long id) {
        Optional<Book> optionalBook = bookRepository.findByIsbn(isbn);
        if (optionalBook.isEmpty()) return false;
        Book book = optionalBook.get();
        return !Objects.requireNonNullElse(book.getId(), true).equals(id);
    }
}
