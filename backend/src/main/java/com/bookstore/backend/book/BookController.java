package com.bookstore.backend.book;

import com.bookstore.backend.awss3.AmazonS3Service;
import com.bookstore.backend.book.dto.BookMetadataRequestDto;
import com.bookstore.backend.book.dto.ThumbnailUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping(value = "api/books", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@CrossOrigin
class BookController {

    private static final Logger log = LoggerFactory.getLogger(BookController.class);
    private final BookService bookService;
    private final AmazonS3Service amazonS3Service;

    @Operation(summary = "Get all books")
    @GetMapping
    Page<Book> all(@ParameterObject Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @Operation(summary = "Get book by ISBN")
    @GetMapping("/{isbn}")
    Book byIsbn(@PathVariable String isbn) {
        return bookService.findByIsbn(isbn);
    }

    @Operation(summary = "Get best sellers")
    @GetMapping("/best-sellers")
    public Page<Book> bestSellers(@ParameterObject Pageable pageable) {
        return bookService.findBestSellers(pageable);
    }

    @Operation(summary = "Search for books by title, author, publisher or supplier")
    @GetMapping("/search")
    public Page<Book> search(@RequestParam String query, @RequestParam String type, @ParameterObject Pageable pageable) {
        log.info("Searching for {} with query: {}", type, query);
        return switch (type) {
            case "title" -> bookService.findByTitleContaining(query, pageable);
            case "author" -> bookService.findByAuthorContaining(query, pageable);
            case "publisher" -> bookService.findByPublisherContaining(query, pageable);
            case "supplier" -> bookService.findBySupplierContaining(query, pageable);
            default -> throw new IllegalArgumentException("Invalid search type");
        };
    }

    @Operation(summary = "Create a new book", security = @SecurityRequirement(name = "token"))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book save(@Valid BookMetadataRequestDto bookMedadataDto) {
        Book book = bookMedadataDto.convertToBook();
        return bookService.save(book);
    }

    @Operation(summary = "Update a book by ISBN", security = @SecurityRequirement(name = "token"))
    @PatchMapping(value = "/{isbn}")
    public Book update(@PathVariable String isbn, @Validated BookMetadataRequestDto bookMetadataDto) {
        var book = bookMetadataDto.convertToBook();
        return bookService.updateByIsbn(isbn, book);
    }

    @Operation(summary = "Upload thumbnails for a book by ISBN", security = @SecurityRequirement(name = "token"))
    @PostMapping(value = "/{isbn}/upload-thumbnail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Book uploadPhoto(@PathVariable String isbn, @ModelAttribute ThumbnailUpdateDto thumbnailsUpdateDto) {
        Book book = bookService.findByIsbn(isbn);
        Set<String> thumbnailsToDelete = new HashSet<>(book.getThumbnails());
        thumbnailsToDelete.removeAll(thumbnailsUpdateDto.getThumbnailsChange());
        thumbnailsToDelete.forEach(amazonS3Service::deleteFile);
        thumbnailsUpdateDto.getThumbnailsAdd().forEach(mf -> {
            String folder = "books/" + isbn + "/thumbnails/";
            String url = amazonS3Service.uploadFile(mf, folder);
            thumbnailsUpdateDto.addThumbnail(url);
        });
        book.setThumbnails(thumbnailsUpdateDto.getThumbnailsChange());
        return bookService.save(book);
    }

    @Operation(summary = "Delete a book by ISBN", security = @SecurityRequirement(name = "token"))
    @DeleteMapping("/{isbn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable String isbn) {
        bookService.deleteByIsbn(isbn);
    }
    
}
