package com.bookstorefullstack.bookstore.book;

import com.bookstorefullstack.bookstore.book.valuetype.CoverType;
import com.bookstorefullstack.bookstore.book.valuetype.Measure;
import com.bookstorefullstack.bookstore.book.valuetype.BookProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("print_book")
@Getter
@Setter
public class PrintBook {
    @Id
    private Long id;
    @Embedded.Nullable
    private BookProperties properties;
    private CoverType coverType;
    @Embedded.Nullable
    private Measure measure;
    private Integer inventory;
}
