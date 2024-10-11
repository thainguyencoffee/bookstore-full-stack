/*
 * @author thainguyencoffee
 */

package com.bookstorefullstack.bookstore.book.dto;

import com.bookstorefullstack.bookstore.book.validator.VietnamesePriceConstraint;
import lombok.Builder;

import java.time.Instant;

@Builder
public record EBookUpdateDto(
        Integer numberOfPages,
        @VietnamesePriceConstraint
        Long originalPrice,
        @VietnamesePriceConstraint
        Long discountedPrice,
        Instant publicationDate,
        Instant releaseDate
) {

    public EBookRequestDto toEBookRequestDto() {
        return EBookRequestDto.builder()
                .numberOfPages(this.numberOfPages)
                .originalPrice(this.originalPrice)
                .discountedPrice(this.discountedPrice)
                .publicationDate(this.publicationDate)
                .releaseDate(this.releaseDate)
                .build();
    }
}
