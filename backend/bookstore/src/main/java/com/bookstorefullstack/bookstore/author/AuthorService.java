package com.bookstorefullstack.bookstore.author;

import com.bookstorefullstack.bookstore.core.exception.CustomNoResultException;
import org.springframework.stereotype.Service;

import static com.bookstorefullstack.bookstore.core.exception.CustomNoResultException.Identifier.ID;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Author findById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new CustomNoResultException(Author.class, ID, id));
    }

}
