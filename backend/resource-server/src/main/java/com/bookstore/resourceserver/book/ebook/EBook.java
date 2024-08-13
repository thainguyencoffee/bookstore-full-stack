package com.bookstore.resourceserver.book.ebook;

import com.bookstore.resourceserver.book.valuetype.BookProperties;
import com.bookstore.resourceserver.book.valuetype.EBookFile;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("ebooks")
@Getter
@Setter
public class EBook {
    @Id
    private Long id;
    private String isbn;
    @Embedded.Empty
    private EBookFile metadata;
    @Embedded.Nullable
    private BookProperties properties;
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
