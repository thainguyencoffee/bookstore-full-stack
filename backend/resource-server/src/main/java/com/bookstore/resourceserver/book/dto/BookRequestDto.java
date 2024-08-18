package com.bookstore.resourceserver.book.dto;

import com.bookstore.resourceserver.book.Book;
import com.bookstore.resourceserver.book.valuetype.Language;
import com.bookstore.resourceserver.book.validator.BookIsbnConstraint;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.util.List;
import java.util.Set;

@Builder
public record BookRequestDto(
        @NotEmpty(message = "The ISBN of book must be empty.")
        @Pattern(regexp = "^([0-9]{10}|[0-9]{13})$", message = "The ISBN of book must contain 13 digits.")
        @BookIsbnConstraint
        String isbn,
        @NotNull(message = "The categoryId must not be null.")
        @Min(value = 1, message = "The categoryId must greater than zero.")
        Long category,
        @NotEmpty(message = "The authorIds list must be non empty.")
        Set<Long> authorIds,
        @NotEmpty(message = "The title of book must be empty.")
        @Size(max = 255, message = "The title of book is too long.")
        String title,
        @NotEmpty(message = "The publisher of book must be empty.")
        @Size(max = 255, message = "The publisher of book is too long")
        String publisher,
        @NotEmpty(message = "The supplier of book must be empty.")
        @Size(max = 255, message = "The supplier of book is too long.")
        String supplier,
        String description,
        @NotEmpty(message = "The language of book must be empty.")
        @Pattern(regexp = "(?i)^(VIETNAMESE|ENGLISH)$", message = "The language is invalid.")
        String language,
        @NotNull(message = "The edition of book must not be null.")
        @Min(value = 1, message = "The edition of book must greater than 1.")
        Integer edition,
        List<String> thumbnails
) {

    public Book buildBook() {
        Book book = new Book();
        book.setIsbn(this.isbn());
        book.setTitle(this.title());
        book.setPublisher(this.publisher());
        book.setSupplier(this.supplier());
        book.setDescription(this.description());
        book.setLanguage(Language.valueOf(this.language()));
        book.setEdition(this.edition());
        book.setThumbnails(this.thumbnails());
        return book;
    }

}
