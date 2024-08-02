package com.bookstore.backend.book;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

/*This is a ref class of Book.class*/
@Table("book_catalog")
@Getter
@Setter
public class CatalogRef {
    private Long catalog;
    private String name;

    public CatalogRef(Long catalog, String name) {
        this.catalog = catalog;
        this.name = name;
    }
}
