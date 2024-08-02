package com.bookstore.backend.book;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

/*This is a ref class of Book.class*/
@Table("book_category")
@Getter
@Setter
public class CategoryRef {
    private Long category;
    private String name;

    public CategoryRef(Long category, String name) {
        this.category = category;
        this.name = name;
    }
}
