package com.bookstore.resourceserver.book.dto.book.ebook;

import com.bookstore.resourceserver.book.valuetype.EBookFile;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
public class EBookRequestDto {

    @NotBlank(message = "The isbn of book must not be null or blank.")
    @Pattern(regexp = "^([0-9]{10}|[0-9]{13})$", message = "The ISBN must be valid")
    private String isbn;
    private EBookFile metadata;
    @NotNull(message = "The number of pages of book must not be null.")
    @Min(value = 3, message = "The number of pages must greater than 3")
    @Max(value = 3000, message = "The number of pages must less than 3000")
    private Integer numberOfPages;
    private Long originalPrice;
    private Long discountedPrice;
    private String currencyPrice;
    private Instant publicationDate;
    private Instant releaseDate;

}
