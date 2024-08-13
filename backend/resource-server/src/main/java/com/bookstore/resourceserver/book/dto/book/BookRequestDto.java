package com.bookstore.resourceserver.book.dto.book;

import com.bookstore.resourceserver.book.valuetype.Language;
import com.bookstore.resourceserver.book.validator.BookIsbnConstraint;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class BookRequestDto {

    @NotBlank(message = "The isbn of book must not be null or blank.")
    @Pattern(regexp = "^([0-9]{10}|[0-9]{13})$", message = "The ISBN must be valid")
    @Size(max = 255, message = "The isbn of book is too long")
    @BookIsbnConstraint
    private String isbn;
    @NotNull(message = "The categoryId must not be null")
    private Long categoryId;
    @NotNull(message = "The author of book must not be null")
    private Set<Long> authorIds;
    @NotBlank(message = "The title of book must not be null or blank.")
    @Size(max = 255, message = "The title of book is too long")
    private String title;
    @NotBlank(message = "The publisher of book must not be null or blank.")
    @Size(max = 255, message = "The publisher of book is too long")
    private String publisher;
    @NotBlank(message = "The supplier of book must not be null or blank.")
    @Size(max = 255, message = "The supplier of book is too long")
    private String supplier;
    private String description;
    @NotNull(message = "The language of book must not be null.")
    private Language language;
    private Integer edition;
    private List<String> thumbnails;

}
