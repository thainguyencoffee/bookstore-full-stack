package com.bookstore.backend.book;

import com.bookstore.backend.book.dto.book.BookMetadataRequestDto;
import com.bookstore.backend.book.dto.book.BookMetadataUpdateDto;
import com.bookstore.backend.book.dto.ThumbnailUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/books", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
class BookController {

    private static final Logger log = LoggerFactory.getLogger(BookController.class);
    private final BookService bookService;

    @Operation(summary = "Get all books")
    @GetMapping
    public Page<Book> all(@ParameterObject Pageable pageable) {
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
    public Book save(@Valid @RequestBody BookMetadataRequestDto bookMetadataDto) {
        return bookService.save(bookMetadataDto);
    }

    @Operation(summary = "Update a book by ISBN", security = @SecurityRequirement(name = "token"))
    @PatchMapping(value = "/{isbn}")
    public Book update(@PathVariable String isbn, @Valid @RequestBody BookMetadataUpdateDto bookMetadataDto) {
        return bookService.updateByIsbn(isbn, bookMetadataDto);
    }

    @Operation(summary = "Upload thumbnails for a book by ISBN", security = @SecurityRequirement(name = "token"))
    @PostMapping(value = "/{isbn}/upload-thumbnail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Book uploadPhoto(@PathVariable String isbn, @ModelAttribute ThumbnailUpdateDto thumbnailsUpdateDto) {
        return bookService.uploadThumbnail(isbn, thumbnailsUpdateDto);
    }

    @Operation(summary = "Delete a book by ISBN", security = @SecurityRequirement(name = "token"))
    @DeleteMapping("/{isbn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable String isbn) {
        bookService.deleteByIsbn(isbn);
    }
    
}
