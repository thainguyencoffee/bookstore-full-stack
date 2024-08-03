package com.bookstore.backend.book;

import lombok.Getter;
import lombok.Setter;

/*This is a ref class of Book.class*/
@Getter
@Setter
public class CategoryRef {
    private Long categoryId;
    private String categoryName;

    public CategoryRef(Long categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }
}
