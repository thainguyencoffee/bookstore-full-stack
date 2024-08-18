/*
 * @author thainguyencoffee
 */

package com.bookstore.resourceserver.book.dto;

import com.bookstore.resourceserver.book.PrintBook;
import com.bookstore.resourceserver.book.validator.VietnamesePriceConstraint;
import com.bookstore.resourceserver.book.valuetype.BookProperties;
import com.bookstore.resourceserver.book.valuetype.CoverType;
import com.bookstore.resourceserver.book.valuetype.Measure;
import com.bookstore.resourceserver.core.valuetype.Price;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.time.Instant;

@Builder
public record PrintBookRequestDto(
        @NotNull(message = "The number of pages of print book must not be null.")
        Integer numberOfPages,
        @NotNull(message = "The original price of print book must not be null.")
        @VietnamesePriceConstraint
        Long originalPrice,
        @VietnamesePriceConstraint
        Long discountedPrice,
        @NotNull(message = "The publication date of print book must not be null.")
        Instant publicationDate,
        @NotNull(message = "The release date of print book must not be null.")
        Instant releaseDate,
        @NotEmpty(message = "The cover type of print book must not be empty.")
        @Pattern(regexp = "(?i)^(PAPERBACK|HARDCOVER)$", message = "The cover type of print book invalid.")
        String coverType,
        @NotNull(message = "The width of print book must not be null.")
        Double width,
        @NotNull(message = "The height of print book must not be null.")
        Double height,
        @NotNull(message = "The thickness of print book must not be null.")
        Double thickness,
        @NotNull(message = "The weight of print book must not be null.")
        Double weight,
        @NotNull(message = "The inventory of print book must not be null.")
        Integer inventory
) {

        public PrintBook buildPrintBook() {
                Measure measure = new Measure(this.width, this.height, this.thickness, this.weight);
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

                PrintBook printBook = new PrintBook();
                printBook.setProperties(bookProperties);
                printBook.setCoverType(CoverType.valueOf(this.coverType));
                printBook.setInventory(this.inventory);
                printBook.setMeasure(measure);
                return printBook;
        }

}
