package com.bookstore.backend.book.dto;

import com.bookstore.backend.book.CoverType;
import com.bookstore.backend.book.Language;
import com.bookstore.backend.book.validator.BookIsbnConstraint;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.*;

@Getter
@Setter
public class BookMetadataRequestDto {

    @NotBlank(message = "The isbn of book must not be null or blank.")
    @Pattern(regexp = "^([0-9]{10}|[0-9]{13})$", message = "The ISBN must be valid")
    @BookIsbnConstraint
    private String isbn;

    @NotNull(message = "The categoryId must not be null")
    private Long categoryId;

    @NotBlank(message = "The title of book must not be null or blank.")
    @Size(max = 255, message = "The title too long")
    private String title;

    @NotBlank(message = "The author of book must not be null or blank.")
    @Size(max = 255, message = "The author name is too long")
    private String author;

    @NotBlank(message = "The publisher of book must not be null or blank.")
    @Size(max = 255, message = "The publisher name is too long")
    private String publisher;

    @NotBlank(message = "The supplier of book must not be null or blank.")
    @Size(max = 255, message = "The supplier name is too long")
    private String supplier;

    private String description;

    @NotNull(message = "The price of book must not be null.")
    @Positive(message = "Value of price must greater than zero")
    private Long price;

    @NotNull(message = "The inventory of book must not be null.")
    @Min(value = 0, message = "The inventory must greater than 0")
    private Integer inventory;

    private Integer purchases;

    @NotNull(message = "The language of book must not be null.")
    private Language language;

    @NotNull(message = "The book cover type must not be null.")
    private CoverType coverType;

    @NotNull(message = "The number of pages of book must not be null.")
    @Min(value = 3, message = "The number of pages must greater than 3")
    @Max(value = 3000, message = "The number of pages must less than 3000")
    private Integer numberOfPages;

    @NotNull(message = "The width of book must not be null")
    @Min(value = 40, message = "The width of book must greater than 40 mm.")
    @Max(value = 300, message = "The width of book must less than 300 mm.")
    private Double width;

    @NotNull(message = "The height of book must not be null.")
    @Min(value = 60, message = "The height of book must greater than 60 mm.")
    @Max(value = 400, message = "The height of book must less than 400 mm.")
    private Double height;

    @NotNull(message = "The thickness of book must not be null.")
    @Min(value = 1, message = "The thickness of book must greater than 1 mm.")
    @Max(value = 100, message = "The thickness of book must less than 100 mm.")
    private Double thickness;

    @NotNull(message = "The weight of book must not be null.")
    @Min(value = 170, message = "The lowest weight of book is 170 grams.")
    private Double weight;

}
