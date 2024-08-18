package com.bookstore.resourceserver.book.dto;

import com.bookstore.resourceserver.book.EBook;
import com.bookstore.resourceserver.book.validator.VietnamesePriceConstraint;
import com.bookstore.resourceserver.book.valuetype.BookProperties;
import com.bookstore.resourceserver.core.valuetype.Price;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.Instant;

@Builder
public record EBookRequestDto(
        @NotNull(message = "The number of pages must not be null.")
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
    public EBook buildEBook() {
        Price price = new Price(
                this.originalPrice(),
                this.discountedPrice()
        );
        if (this.discountedPrice() == null) {
            price.setDiscountedPrice(0L);
        }

        BookProperties bookProperties = new BookProperties();
        bookProperties.setNumberOfPages(this.numberOfPages());
        bookProperties.setPrice(price);
        bookProperties.setPublicationDate(this.publicationDate());
        bookProperties.setReleaseDate(this.releaseDate());
        bookProperties.setPurchases(0);

        EBook ebook = new EBook();
        ebook.setProperties(bookProperties);
        return ebook;
    }
}
