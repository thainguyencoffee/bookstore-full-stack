package com.bookstore.resourceserver.purchaseorder;

import com.bookstore.resourceserver.purchaseorder.valuetype.BookType;
import lombok.Data;

@Data
public class VariantBookRef {
    private Long detailId;
    private String isbn;
    private String title;
    private BookType bookType;

    public VariantBookRef(Long detailId, String isbn, String title) {
        this.detailId = detailId;
        this.isbn = isbn;
        this.title = title;
    }

}
