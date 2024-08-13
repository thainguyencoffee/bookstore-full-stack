package com.bookstore.resourceserver.book.printbook;

import com.bookstore.resourceserver.book.valuetype.CoverType;
import com.bookstore.resourceserver.book.valuetype.Measure;
import com.bookstore.resourceserver.book.valuetype.BookProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("print_books")
@Getter
@Setter
public class PrintBook {
    @Id
    private Long id;
    private String isbn;
    @Embedded.Nullable
    private BookProperties properties;
    private CoverType coverType;
    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
    private Measure measure;
    private Integer inventory;
    @CreatedDate
    private Instant createdDate;
    @CreatedBy
    private String createdBy;
    @LastModifiedDate
    private Instant lastModifiedDate;
    @LastModifiedBy
    private String lastModifiedBy;
    @Version
    private int version;
}
