package com.bookstore.resourceserver.book.dto.book.printbook;

import com.bookstore.resourceserver.book.valuetype.CoverType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
public class PrintBookRequestDto {

    @NotBlank(message = "The isbn of book must not be null or blank.")
    @Pattern(regexp = "^([0-9]{10}|[0-9]{13})$", message = "The ISBN must be valid")
    private String isbn;
    @NotNull(message = "The number of pages of book must not be null.")
    @Min(value = 3, message = "The number of pages must greater than 3")
    @Max(value = 3000, message = "The number of pages must less than 3000")
    private Integer numberOfPages;
    @NotNull(message = "The original price date must not be null.")
    private Long originalPrice;
    @NotNull(message = "The discounted price date must not be null.")
    private Long discountedPrice;
    @NotBlank(message = "The currency price date must not be null.")
    private String currencyPrice;
    @NotNull(message = "The publication date must not be null.")
    private Instant publicationDate;
    @NotNull(message = "The release date must not be null.")
    private Instant releaseDate;
    @NotNull(message = "The book cover type must not be null.")
    private CoverType coverType;
    @NotNull(message = "The width of book must not be null")
    private Double width;
    @NotNull(message = "The height of book must not be null.")
    private Double height;
    @NotNull(message = "The thickness of book must not be null.")
    private Double thickness;
    @NotNull(message = "The weight of book must not be null.")
    private Double weight;
    @NotNull(message = "The inventory of book must not be null.")
    private Integer inventory;

}
