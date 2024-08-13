package com.bookstore.resourceserver.book.printbook;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface PrintBookRepository extends ListCrudRepository<PrintBook, Long> {

    Optional<PrintBook> findByIsbn(String isbn);

    Page<PrintBook> findAll(Pageable pageable);

    void deleteByIsbn(String isbn);

}
