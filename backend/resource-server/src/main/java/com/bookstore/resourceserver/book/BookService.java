package com.bookstore.resourceserver.book;

import com.bookstore.resourceserver.awss3.MultiMediaService;
import com.bookstore.resourceserver.book.dto.book.BookMetadataRequestDto;
import com.bookstore.resourceserver.book.dto.book.BookMetadataUpdateDto;
import com.bookstore.resourceserver.core.exception.CustomNoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryService categoryService;
    private final MultiMediaService multiMediaService;

    public Book findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn).orElseThrow(() -> new CustomNoResultException(Book.class, CustomNoResultException.Identifier.ISBN, isbn));
    }

    public Book save(BookMetadataRequestDto dto) {
        Category category = categoryService.findById(dto.getCategoryId());
        Book book = convertToBook(dto, category);
        return bookRepository.save(book);
    }

    public void save(Book book) {
        bookRepository.save(book);
    }

    public Page<Book> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Book updateByIsbn(String isbn, BookMetadataUpdateDto patch) {
        Book book = findByIsbn(isbn);
        Optional.ofNullable(patch.getCategoryId()).ifPresent(categoryId -> book.setCategory(categoryService.findById(categoryId)));
        Optional.ofNullable(patch.getTitle()).ifPresent(book::setTitle);
        Optional.ofNullable(patch.getAuthor()).ifPresent(book::setAuthor);
        Optional.ofNullable(patch.getPublisher()).ifPresent(book::setPublisher);
        Optional.ofNullable(patch.getSupplier()).ifPresent(book::setSupplier);
        Optional.ofNullable(patch.getPrice()).ifPresent(book::setPrice);
        Optional.ofNullable(patch.getInventory()).ifPresent(book::setInventory);
        Optional.ofNullable(patch.getCoverType()).ifPresent(book::setCoverType);
        Optional.ofNullable(patch.getLanguage()).ifPresent(book::setLanguage);
        Optional.ofNullable(patch.getDescription()).ifPresent(book::setDescription);
        Optional.ofNullable(patch.getPurchases()).ifPresent(book::setPurchases);
        Optional.ofNullable(patch.getNumberOfPages()).ifPresent(book::setNumberOfPages);
        if (patch.getThumbnails() != null) {
            book.setThumbnails(updateThumbnails(book.getThumbnails(), patch.getThumbnails()));
        }
        Measure measure = patchMeasure(patch, book);
        book.setMeasure(measure);
        return bookRepository.save(book);
    }

    private List<String> updateThumbnails(List<String> currentThumbnails, List<String> newThumbnails) {
        if (!currentThumbnails.isEmpty()) {
            currentThumbnails.removeAll(newThumbnails);
            multiMediaService.deleteEverything(currentThumbnails);
        }
        return newThumbnails;
    }

    private Measure patchMeasure(BookMetadataUpdateDto patch, Book book) {
        Measure measure = book.getMeasure();
        if (patch.getWidth() != null)
            measure.setWidth(patch.getWidth());
        if (patch.getHeight() != null)
            measure.setHeight(patch.getHeight());
        if (patch.getThickness() != null)
            measure.setThickness(patch.getThickness());
        if (patch.getWeight() != null)
            measure.setWeight(patch.getWeight());
        return measure;
    }

    public void deleteByIsbn(String isbn) {
        bookRepository.deleteByIsbn(isbn);
    }

    public Page<Book> findBestSellers(Instant statisticIn, Pageable pageable) {
        return bookRepository.findByPurchaseAtAfterOrderByPurchasesDesc(statisticIn, pageable);
    }

    private static Book convertToBook(BookMetadataRequestDto dto, Category category) {
        Book book = new Book();
        book.setPurchases(dto.getPurchases() != null && dto.getPurchases() > 0 ? dto.getPurchases() : 0);
        book.setIsbn(dto.getIsbn());
        book.setCategory(category);
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setPublisher(dto.getPublisher());
        book.setSupplier(dto.getSupplier());
        book.setDescription(dto.getDescription());
        book.setPrice(dto.getPrice());
        book.setInventory(dto.getInventory());
        book.setLanguage(dto.getLanguage());
        book.setCoverType(dto.getCoverType());
        book.setNumberOfPages(dto.getNumberOfPages());
        book.setMeasure(new Measure(dto.getWidth(), dto.getHeight(), dto.getThickness(), dto.getWeight()));
        book.setThumbnails(dto.getThumbnails());
        return book;
    }

}
