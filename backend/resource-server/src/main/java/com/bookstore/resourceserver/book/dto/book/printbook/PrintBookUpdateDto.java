package com.bookstore.resourceserver.book.dto.book.printbook;

import com.bookstore.resourceserver.book.valuetype.CoverType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class PrintBookUpdateDto {

    @Min(value = 3, message = "The number of pages must greater than 3")
    @Max(value = 3000, message = "The number of pages must less than 3000")
    private Integer numberOfPages;
    private Long originalPrice;
    private Long discountedPrice;
    private String currencyPrice;
    private Instant publicationDate;
    private Instant releaseDate;
    private CoverType coverType;
    private Double width;
    private Double height;
    private Double thickness;
    private Double weight;
    private Integer inventory;

}
