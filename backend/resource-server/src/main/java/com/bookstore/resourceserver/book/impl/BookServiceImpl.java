package com.bookstore.resourceserver.book.impl;

import com.bookstore.resourceserver.awss3.MultiMediaService;
import com.bookstore.resourceserver.book.Book;
import com.bookstore.resourceserver.book.BookRepository;
import com.bookstore.resourceserver.book.BookService;
import com.bookstore.resourceserver.book.author.Author;
import com.bookstore.resourceserver.book.author.AuthorService;
import com.bookstore.resourceserver.book.category.Category;
import com.bookstore.resourceserver.book.category.CategoryService;
import com.bookstore.resourceserver.book.dto.book.BookRequestDto;
import com.bookstore.resourceserver.book.dto.book.BookUpdateDto;
import com.bookstore.resourceserver.core.exception.CustomNoResultException;
import com.bookstore.resourceserver.core.exception.EntityCastException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService<Book> {

    private final BookRepository bookRepository;
    private final CategoryService categoryService;
    private final AuthorService authorService;
    private final MultiMediaService multiMediaService;

    @Override
    public Book findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() ->
                        new CustomNoResultException(
                                Book.class,
                                CustomNoResultException.Identifier.ISBN, isbn));
    }

    @Override
    public Page<Book> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Override
    public Book save(Object dto) {
        if (dto instanceof BookRequestDto bookRequestDto) {
            Book book = convertToBook(bookRequestDto);
            return bookRepository.save(book);
        } else {
            throw new EntityCastException(dto.getClass(), BookRequestDto.class);
        }
    }

    public void save(Book book) {
        bookRepository.save(book);
    }

    @Override
    public Book updateByIsbn(String isbn, Object dto) {
        if (dto instanceof BookUpdateDto bookUpdateDto) {
            var book = findByIsbn(isbn);
            Optional.ofNullable(bookUpdateDto.getCategoryId()).ifPresent(categoryId -> book.setCategory(categoryService.findById(categoryId)));
            Optional.ofNullable(bookUpdateDto.getTitle()).ifPresent(book::setTitle);
            Optional.ofNullable(bookUpdateDto.getPublisher()).ifPresent(book::setPublisher);
            Optional.ofNullable(bookUpdateDto.getSupplier()).ifPresent(book::setSupplier);
            Optional.ofNullable(bookUpdateDto.getDescription()).ifPresent(book::setDescription);
            Optional.ofNullable(bookUpdateDto.getLanguage()).ifPresent(book::setLanguage);
            Optional.ofNullable(bookUpdateDto.getEdition()).ifPresent(book::setEdition);
            if (bookUpdateDto.getAuthorIds() != null) {
                Set<Author> authorSet = new HashSet<>();
                for (Long authorId : bookUpdateDto.getAuthorIds()) {
                    Author author = authorService.findById(authorId);
                    authorSet.add(author);
                }
                book.setAuthors(authorSet);
            }

            if (bookUpdateDto.getThumbnails() != null) {
                book.setThumbnails(updateThumbnails(book.getThumbnails(), bookUpdateDto.getThumbnails()));
            }
            return bookRepository.save(book);
        } else {
            throw new EntityCastException(dto.getClass(), BookUpdateDto.class);
        }
    }

    private List<String> updateThumbnails(List<String> currentThumbnails, List<String> newThumbnails) {
        if (!currentThumbnails.isEmpty()) {
            currentThumbnails.removeAll(newThumbnails);
            multiMediaService.deleteEverything(currentThumbnails);
        }
        return newThumbnails;
    }

    @Override
    public void deleteByIsbn(String isbn) {
        bookRepository.deleteByIsbn(isbn);
    }

//    public Page<Book> findBestSellers(Instant statisticIn, Pageable pageable) {
//        return bookRepository.findByPurchaseAtAfterOrderByPurchasesDesc(statisticIn, pageable);
//    }

    private Book convertToBook(BookRequestDto dto) {
        Book book = new Book();
        book.setIsbn(dto.getIsbn());
        Category category = categoryService.findById(dto.getCategoryId());
        book.setCategory(category);
        for (Long authorId : dto.getAuthorIds()) {
            Author author = authorService.findById(authorId);
            book.setAuthor(author);
        }

        book.setTitle(dto.getTitle());
        book.setPublisher(dto.getPublisher());
        book.setSupplier(dto.getSupplier());
        book.setDescription(dto.getDescription());
        book.setLanguage(dto.getLanguage());
        book.setEdition(dto.getEdition());
        book.setThumbnails(dto.getThumbnails());
        return book;
    }
}
