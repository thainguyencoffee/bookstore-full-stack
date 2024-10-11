package com.bookstorefullstack.bookstore.book.dto;

import com.bookstorefullstack.bookstore.book.validator.BookIsbnConstraint;
import com.bookstorefullstack.bookstore.book.validator.VietnamesePriceConstraint;
import com.bookstorefullstack.bookstore.book.valuetype.Language;
import jakarta.validation.constraints.*;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public record BookAndEBookRequestDto (
        @Pattern(regexp = "^([0-9]{10}|[0-9]{13})$", message = "The ISBN of book must not blank and contain 13 digits.")
        @BookIsbnConstraint
        String isbn,
        @NotNull(message = "The category id must not be null")
        Long category,
        @NotNull(message = "The author of book must not be null")
        Set<Long> authorIds,
        @NotBlank(message = "The title of book must not be null or blank.")
        @Size(max = 255, message = "The title of book is too long")
        String title,
        @NotBlank(message = "The publisher of book must not be null or blank.")
        @Size(max = 255, message = "The publisher of book is too long")
        String publisher,
        @NotBlank(message = "The supplier of book must not be null or blank.")
        @Size(max = 255, message = "The supplier of book is too long")
        String supplier,
        String description,
        @NotNull(message = "The language of book must not be null.")
        Language language,
        Integer edition,
        List<String> thumbnails,
        @NotBlank(message = "The url of the ebook file must not be null or blank.")
        String url,
        @NotNull(message = "The file size of the ebook file must not be null.")
        @Positive(message = "The file size of the ebook must be positive number.")
        Integer fileSize,
        @Pattern(regexp = "(?i)^(pdf|epub)$", message = "The format invalid.")
        String format,
        @NotNull(message = "The number of pages must not be null.")
        @Negative(message = "The number of pages must greater than zero.")
        Integer numberOfPages,
        @NotNull(message = "The original price of the ebook must not be null.")
        @VietnamesePriceConstraint
        Long originalPrice,
        @VietnamesePriceConstraint
        Long discountedPrice,
        @NotNull(message = "The publication date must not be null.")
        Instant publicationDate,
        @NotNull(message = "The release date must not be null.")
        Instant releaseDate
) {

}
