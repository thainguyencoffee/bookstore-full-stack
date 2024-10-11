package com.bookstorefullstack.bookstore.purchaseorder;

import com.bookstorefullstack.bookstore.purchaseorder.valuetype.BookType;
import lombok.Data;

@Data
public class VariantBookRef {
    private String isbn;
    private String title;
    private BookType bookType;

    public VariantBookRef(String isbn, String title) {
        this.isbn = isbn;
        this.title = title;
    }

}
