package com.bookstore.resourceserver.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService<T> {

    T findByIsbn(String isbn);

    Page<T> findAll(Pageable pageable);

    T save(Object dto);

    T updateByIsbn(String isbn, Object dto);

    void deleteByIsbn(String isbn);

}
