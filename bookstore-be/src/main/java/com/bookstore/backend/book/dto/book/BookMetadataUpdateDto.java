package com.bookstore.backend.book.dto.book;

import com.bookstore.backend.book.CoverType;
import com.bookstore.backend.book.Language;
import com.bookstore.backend.book.validator.NumberConstraint;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookMetadataUpdateDto {

    private Long categoryId;

    @Size(max = 255, message = "The title too long")
    private String title;

    @Size(max = 255, message = "The author name is too long")
    private String author;

    @Size(max = 255, message = "The publisher name is too long")
    private String publisher;

    @Size(max = 255, message = "The supplier name is too long")
    private String supplier;

    private String description;

    @Positive(message = "Value of price must greater than zero")
    private Long price;

    @NumberConstraint(min = 0, message = "The inventory must greater than 0")
    private Integer inventory;

    private Integer purchases;

    private Language language;

    private CoverType coverType;

    @NumberConstraint(min = 3, max = 3000, message = "The number of pages must between 3 and 3000")
    private Integer numberOfPages;

    @NumberConstraint(min = 40, max = 300, message = "The width of book must between 40 and 300 mm")
    private Double width;

    @NumberConstraint(min = 60, max = 400, message = "The height of book must between 60 and 400 mm")
    private Double height;

    @NumberConstraint(min = 1, max = 100, message = "The thickness of book must between 1 and 100 mm")
    private Double thickness;

    @NumberConstraint(min = 170, message = "The lowest weight of book is 170 grams.")
    private Double weight;

    private List<String> thumbnails;
}
