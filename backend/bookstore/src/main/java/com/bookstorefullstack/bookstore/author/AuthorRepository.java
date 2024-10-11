package com.bookstorefullstack.bookstore.author;

import org.springframework.data.repository.ListCrudRepository;

public interface AuthorRepository extends ListCrudRepository<Author, Long> {
}
