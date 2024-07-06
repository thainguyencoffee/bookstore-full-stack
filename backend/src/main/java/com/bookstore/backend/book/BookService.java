package com.bookstore.backend.book;

import com.bookstore.backend.book.exception.BookNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn).orElseThrow(() -> new BookNotFoundException(isbn));
    }

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public Iterable<Book> saveAll(List<Book> books) {
        return bookRepository.saveAll(books);
    }

    public void deleteAll() {
        bookRepository.deleteAll();
    }
}
