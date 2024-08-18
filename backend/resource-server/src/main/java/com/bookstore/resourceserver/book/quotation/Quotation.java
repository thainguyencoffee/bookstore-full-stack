package com.bookstore.resourceserver.book.quotation;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("quotation")
@Getter
@Setter
public class Quotation {

    @Id
    private Long id;
    private String isbn;
    private String text;
    private Long authorId;
    private String jobTitle;
    @CreatedDate
    private Instant createdDate;
    @CreatedBy
    private String createdBy;
    @LastModifiedDate
    private Instant lastModifiedDate;
    @LastModifiedBy
    private String lastModifiedBy;

}