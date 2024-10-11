package com.bookstorefullstack.bookstore.book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("book_author")
@AllArgsConstructor
public class AuthorRef {

    private Long author;
    private String authorName;
}
