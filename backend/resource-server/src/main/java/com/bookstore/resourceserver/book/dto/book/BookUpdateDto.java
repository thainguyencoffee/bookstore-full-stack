package com.bookstore.resourceserver.book.dto.book;

import com.bookstore.resourceserver.book.valuetype.Language;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class BookUpdateDto {

    private Long categoryId;
    private Set<Long> authorIds;
    @Size(max = 255, message = "The title too long")
    private String title;
    @Size(max = 255, message = "The publisher name is too long")
    private String publisher;
    @Size(max = 255, message = "The supplier name is too long")
    private String supplier;
    private String description;
    private Language language;
    private Integer edition;
    private List<String> thumbnails;

}
