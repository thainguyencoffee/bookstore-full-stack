package com.bookstore.backend.book;

import com.bookstore.backend.core.exception.CustomNoResultException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn).orElseThrow(() -> new CustomNoResultException(Book.class, CustomNoResultException.Identifier.ISBN, isbn));
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

    public Page<Book> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Book updateByIsbn(String isbn, Book patch) {
        Book book = findByIsbn(isbn);
        if (patch.getTitle() != null)
            book.setTitle(patch.getTitle());
        if (patch.getAuthor() != null)
            book.setAuthor(patch.getAuthor());
        if (patch.getPublisher() != null) 
            book.setPublisher(patch.getPublisher());
        if (patch.getSupplier() != null)
            book.setSupplier(patch.getSupplier());
        if (patch.getPrice() != null)
            book.setPrice(patch.getPrice());
        if (patch.getInventory() != null)
            book.setInventory(patch.getInventory());
        if (patch.getCoverType() != null) 
            book.setCoverType(patch.getCoverType());
        if (patch.getLanguage() != null)
            book.setLanguage(patch.getLanguage());
        Measure measure = patchMeasure(patch, book);
        book.setMeasure(measure);
        if (patch.getPurchases() != null)
            book.setPurchases(patch.getPurchases());
        if (patch.getNumberOfPages() != null)
            book.setNumberOfPages(patch.getNumberOfPages());
        return bookRepository.save(book);
    }

    private Measure patchMeasure(Book patch, Book book) {
        Measure measure = book.getMeasure();
        if (patch.getMeasure().getWidth() != 0)
            measure.setWidth(patch.getMeasure().getWidth());
        if (patch.getMeasure().getHeight() != 0)
            measure.setHeight(patch.getMeasure().getHeight());
        if (patch.getMeasure().getThickness() != 0)
            measure.setThickness(patch.getMeasure().getThickness());
        if (patch.getMeasure().getWeight() != 0)
            measure.setWeight(patch.getMeasure().getWeight());
        return measure;
    }

    public void deleteByIsbn(String isbn) {
        bookRepository.deleteByIsbn(isbn);
    }

    public Page<Book> findBestSellers(Pageable pageable) {
        return bookRepository.findAllByOrderByPurchasesDesc(pageable);
    }

    public Page<Book> findByTitleContaining(String query, Pageable pageable) {
        return bookRepository.findAllByTitleContaining(query, pageable);
    }

    public Page<Book> findByAuthorContaining(String query, Pageable pageable) {
        return bookRepository.findAllByAuthorContaining(query, pageable);
    }

    public Page<Book> findByPublisherContaining(String query, Pageable pageable) {
        return bookRepository.findAllByPublisherContaining(query, pageable);
    }

    public Page<Book> findBySupplierContaining(String query, Pageable pageable) {
        return bookRepository.findAllBySupplierContaining(query, pageable);
    }
}
