package com.bookstore.backend.book.dto.category;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryMetadataUpdateDto {
    @Size(max = 255, message = "The name of category is too long")
    private String name;

    private Long parentId;

    private String thumbnail;


    public CategoryMetadataUpdateDto() {
    }

    public CategoryMetadataUpdateDto(String name, Long parentId) {
        this.name = name;
        this.parentId = parentId;
    }
}
