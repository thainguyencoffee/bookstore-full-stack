package com.bookstore.resourceserver.author;

import org.springframework.data.repository.ListCrudRepository;

public interface AuthorRepository extends ListCrudRepository<Author, Long> {
}
