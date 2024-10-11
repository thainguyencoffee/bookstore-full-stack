/*
 * @author thainguyencoffee
 */

package com.bookstorefullstack.bookstore.book.dto;

import com.bookstorefullstack.bookstore.book.validator.VietnamesePriceConstraint;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.time.Instant;

@Builder
public record PrintBookUpdateDto(
//        @NotNull(message = "The number of pages of print book must not be null.")
        Integer numberOfPages,
//        @NotNull(message = "The original price of print book must not be null.")
        @VietnamesePriceConstraint
        Long originalPrice,
        @VietnamesePriceConstraint
        Long discountedPrice,
//        @NotNull(message = "The publication date of print book must not be null.")
        Instant publicationDate,
//        @NotNull(message = "The release date of print book must not be null.")
        Instant releaseDate,
//        @NotEmpty(message = "The cover type of print book must not be empty.")
        @Pattern(regexp = "(?i)^(PAPERBACK|HARDCOVER)$", message = "The cover type of print book invalid.")
        String coverType,
//        @NotNull(message = "The width of print book must not be null.")
        Double width,
//        @NotNull(message = "The height of print book must not be null.")
        Double height,
//        @NotNull(message = "The thickness of print book must not be null.")
        Double thickness,
//        @NotNull(message = "The weight of print book must not be null.")
        Double weight,
//        @NotNull(message = "The inventory of print book must not be null.")
        Integer inventory
) {

        public PrintBookRequestDto toPrintBookRequestDto() {
                return PrintBookRequestDto.builder()
                        .numberOfPages(numberOfPages)
                        .originalPrice(originalPrice)
                        .discountedPrice(discountedPrice)
                        .publicationDate(publicationDate)
                        .releaseDate(releaseDate)
                        .coverType(coverType)
                        .width(width)
                        .height(height)
                        .thickness(thickness)
                        .weight(weight)
                        .inventory(inventory)
                        .build();
        }
}
