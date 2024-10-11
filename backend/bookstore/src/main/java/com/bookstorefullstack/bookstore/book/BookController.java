package com.bookstorefullstack.bookstore.book;

import com.bookstorefullstack.bookstore.awss3.MultiMediaService;
import com.bookstorefullstack.bookstore.book.dto.*;
import com.bookstorefullstack.bookstore.book.dto.view.BookSalesView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "books", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
class BookController {

    private final BookService bookService;
    private final MultiMediaService multiMediaService;

    @Operation(summary = "Get all books")
    @GetMapping
    public Page<Book> all(@ParameterObject Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @Operation(summary = "Get book by ISBN")
    @GetMapping("/{isbn}")
    public Book byIsbn(@PathVariable String isbn) {
        return bookService.findByIsbn(isbn);
    }

    @Operation(summary = "Get best sellers")
    @GetMapping("/best-sellers")
    public List<BookSalesView> bestSellers(@RequestParam("top") Integer top) {
        return bookService.findBestSellers(top);
    }

    @Operation(summary = "Create a new book", security = @SecurityRequirement(name = "token"))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book save(@Valid @RequestBody BookRequestDto bookMetadataDto) {
        return bookService.save(bookMetadataDto);
    }

    @Operation(summary = "Update a book by ISBN", security = @SecurityRequirement(name = "token"))
    @PatchMapping(value = "/{isbn}")
    public Book update(@PathVariable String isbn, @Valid @RequestBody BookUpdateDto bookMetadataDto) {
        return bookService.updateBookByIsbn(isbn, bookMetadataDto);
    }

    @Operation(summary = "Upload thumbnails for a book by ISBN", security = @SecurityRequirement(name = "token"))
    @PostMapping(value = "/{isbn}/thumbnails", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<String> uploadThumbnail(@PathVariable String isbn, @RequestPart List<MultipartFile> thumbnails) {
        return multiMediaService.uploadEverything(Book.class, isbn, thumbnails, "thumbnail");
    }

    @Operation(summary = "Delete a book by ISBN", security = @SecurityRequirement(name = "token"))
    @DeleteMapping("/{isbn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String isbn) {
        bookService.deleteByIsbn(isbn);
    }


    @Operation(summary = "Create a new eBook for isbn", security = @SecurityRequirement(name = "token"))
    @PostMapping("/{isbn}/ebooks")
    @ResponseStatus(HttpStatus.CREATED)
    public Book saveEBook(@PathVariable String isbn, @Valid @RequestBody EBookRequestDto eBookRequestDto) {
        return bookService.saveEBook(isbn, eBookRequestDto);
    }

    @PatchMapping("/{isbn}/ebooks")
    @ResponseStatus(HttpStatus.OK)
    public Book updateEBookByIsbnAndId(@PathVariable String isbn,
                                       @Valid @RequestBody EBookUpdateDto eBookUpdateDto) {
        return bookService.updateEBookByIsbn(isbn, eBookUpdateDto);
    }

    @DeleteMapping("/{isbn}/ebooks")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEBookByIsbnAndId(@PathVariable String isbn) {
        bookService.deleteEBookByIsbnAndId(isbn);
    }

    @PostMapping("/{isbn}/print-books")
    @ResponseStatus(HttpStatus.CREATED)
    public Book savePrintBook(@PathVariable String isbn, @Valid @RequestBody PrintBookRequestDto printBookRequestDto) {
        return bookService.savePrintBook(isbn, printBookRequestDto);
    }

    @PatchMapping("/{isbn}/print-books")
    @ResponseStatus(HttpStatus.OK)
    public Book updatePrintBookByIsbnAndId(@PathVariable String isbn, @Valid @RequestBody PrintBookUpdateDto printBookUpdateDto) {
        return bookService.updatePrintBookByIsbnAndId(isbn, printBookUpdateDto);
    }

    @DeleteMapping("/{isbn}/print-books")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePrintBookByIsbnAndId(@PathVariable String isbn) {
        bookService.deletePrintBookByIsbnAndId(isbn);
    }

}
