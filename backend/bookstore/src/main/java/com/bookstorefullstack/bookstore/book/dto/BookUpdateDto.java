/*
 * @author thainguyencoffee
 */
package com.bookstorefullstack.bookstore.book.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.util.List;
import java.util.Set;

@Builder
public record BookUpdateDto (
        @Min(value = 1, message = "The categoryId must greater than zero.")
        Long category,
        Set<Long> authorIds,
        @Size(max = 255, message = "The title too long")
        String title,
        @Size(max = 255, message = "The publisher name is too long")
        String publisher,
        @Size(max = 255, message = "The supplier name is too long")
        String supplier,
        String description,
        @Pattern(regexp = "(?i)^(VIETNAMESE|ENGLISH)$", message = "The language is invalid.")
        String language,
        @Min(value = 1, message = "The edition of book must greater than 1.")
        Integer edition,
        List<String> thumbnails
) {

}
