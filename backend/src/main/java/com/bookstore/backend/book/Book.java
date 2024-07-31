package com.bookstore.backend.book;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@Table("books")
@Builder
public class Book {

    @Id
    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private String supplier;
    private String description;
    private Long price;
    private Integer inventory;
    private Language language;
    private CoverType coverType;
    private Integer numberOfPages;
    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
    private Measure measure;
    private Set<String> thumbnails;
    private Integer purchases;
    @CreatedDate
    private Instant createdAt;
    @CreatedBy
    private String createdBy;
    @LastModifiedDate
    private Instant lastModifiedAt;
    @LastModifiedBy
    private String lastModifiedBy;
    @Version
    private int version;
}
