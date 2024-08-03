package com.bookstore.backend.book;

import com.bookstore.backend.awss3.AmazonS3Service;
import com.bookstore.backend.book.dto.BookMetadataRequestDto;
import com.bookstore.backend.book.dto.BookMetadataUpdateDto;
import com.bookstore.backend.book.dto.ThumbnailUpdateDto;
import com.bookstore.backend.core.exception.CustomNoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryService categoryService;
    private final AmazonS3Service amazonS3Service;

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

    public Iterable<Book> saveAll(List<Book> books) {
        return bookRepository.saveAll(books);
    }

    public Page<Book> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Book updateByIsbn(String isbn, BookMetadataUpdateDto patch) {
        Book book = findByIsbn(isbn);
        if (patch.getCategoryId() != null) {
            Category category = categoryService.findById(patch.getCategoryId());
            book.setCategory(category);
        }
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
        Book save = bookRepository.save(book);
        return save;
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

    public Book uploadThumbnail(String isbn, ThumbnailUpdateDto thumbnailsUpdateDto) {
        Book book = findByIsbn(isbn);
        Set<String> thumbnailsToDelete = new HashSet<>(book.getThumbnails());
        thumbnailsToDelete.removeAll(thumbnailsUpdateDto.getThumbnailsChange());
        thumbnailsToDelete.forEach(amazonS3Service::deleteFile);
        if (thumbnailsUpdateDto.getThumbnailsAdd() != null) {
            thumbnailsUpdateDto.getThumbnailsAdd().forEach(mf -> {
                String folder = "books/" + isbn + "/thumbnails/";
                String url = amazonS3Service.uploadFile(mf, folder);
                thumbnailsUpdateDto.addThumbnail(url);
            });
        }
        book.setThumbnails(thumbnailsUpdateDto.getThumbnailsChange());
        return bookRepository.save(book);
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
        return book;
    }
}
