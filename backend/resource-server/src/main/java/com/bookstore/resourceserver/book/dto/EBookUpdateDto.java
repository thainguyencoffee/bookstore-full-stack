/*
 * @author thainguyencoffee
 */

package com.bookstore.resourceserver.book.dto;

import com.bookstore.resourceserver.book.validator.VietnamesePriceConstraint;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.time.Instant;

@Builder
public record EBookUpdateDto (
        String url,
        Integer fileSize,
        @Pattern(regexp = "(?i)^(pdf|epub)$", message = "The format of ebook invalid.")
        String format,
        Integer numberOfPages,
        @VietnamesePriceConstraint
        Long originalPrice,
        @VietnamesePriceConstraint
        Long discountedPrice,
        Instant publicationDate,
        Instant releaseDate
) {
}
