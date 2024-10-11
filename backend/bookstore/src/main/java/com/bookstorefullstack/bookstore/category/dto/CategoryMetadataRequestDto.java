package com.bookstorefullstack.bookstore.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryMetadataRequestDto {
    @NotBlank(message = "The name of category must not be null")
    @Size(max = 255, message = "The name of category is too long")
    private String name;

    private Long parentId;

    private String thumbnail;

    public CategoryMetadataRequestDto(String name, Long parentId) {
        this.name = name;
        this.parentId = parentId;
    }
}
