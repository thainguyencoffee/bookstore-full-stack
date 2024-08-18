package com.bookstore.resourceserver.book;

import lombok.Getter;
import lombok.Setter;

/*This is a ref class of Book.class*/
@Getter
@Setter
public class CategoryRef {
    private Long category;
    private String categoryName;

    public CategoryRef(Long category, String categoryName) {
        this.category = category;
        this.categoryName = categoryName;
    }
}
