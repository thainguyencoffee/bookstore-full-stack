package com.bookstore.resourceserver.book.author;

import org.springframework.data.repository.ListCrudRepository;

public interface AuthorRepository extends ListCrudRepository<Author, Long> {
}
