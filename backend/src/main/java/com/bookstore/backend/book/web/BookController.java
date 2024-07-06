package com.bookstore.backend.book.web;

import com.bookstore.backend.book.BookService;
import org.springframework.web.bind.annotation.RestController;

@RestController
class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

}
