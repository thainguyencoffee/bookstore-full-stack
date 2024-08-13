package com.bookstore.resourceserver.book.ebook;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface EBookRepository extends ListCrudRepository<EBook, Long> {

    Optional<EBook> findByIsbn(String isbn);

    Page<EBook> findAll(Pageable pageable);

    void deleteByIsbn(String isbn);

}
