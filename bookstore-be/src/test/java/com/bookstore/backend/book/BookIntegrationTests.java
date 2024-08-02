package com.bookstore.backend.book;

import com.bookstore.backend.IntegrationTestsBase;
import com.bookstore.backend.book.dto.BookMetadataRequestDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

class BookIntegrationTests extends IntegrationTestsBase {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    private WebTestClient webTestClient;

    @AfterEach
    public void tearDown() {
        bookRepository.deleteAll();
    }

    @Test
    void whenGetBookThenOk() {
        webTestClient
                .get().uri("/api/books")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Book.class)
                .returnResult().getResponseBody();
    }

    @Test
    void whenOtherMethodToBookUrlThenUnauthenticated() {
        Book book = new Book();
        book.setIsbn("1234567891");
        book.setTitle("Title 1");
        book.setAuthor("Author 1");
        book.setPublisher("Publisher 1");
        book.setSupplier("Supplier 1");
        book.setPrice(210000L);
        book.setLanguage(Language.ENGLISH);
        book.setCoverType(CoverType.PAPERBACK);
        book.setNumberOfPages(25);
        book.setMeasure(new Measure(120, 180, 10, 200));
        webTestClient
                .post().uri("/api/books")
                .bodyValue(book)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void whenAuthenticatedPostBookThenReturn() {
        String isbn = "1234567891";
        BookMetadataRequestDto book = new BookMetadataRequestDto();
        book.setIsbn(isbn);
        book.setTitle("The Art of Computer Programming");
        book.setAuthor("Donald Knuth");
        book.setPublisher("Addison-Wesley");
        book.setSupplier("Amazon");
        book.setDescription("The Art of Computer Programming is a comprehensive monograph " +
                "written by Donald Knuth that covers many kinds of programming algorithms and their analysis.");
        book.setPrice(10000L);
        book.setInventory(100);
        book.setInventory(100);
        book.setLanguage(Language.ENGLISH);
        book.setCoverType(CoverType.HARDCOVER);
        book.setNumberOfPages(1000);
        book.setWidth(300);
        book.setHeight(400);
        book.setThickness(100);
        book.setWeight(170);

        webTestClient
                .post().uri("/api/books")
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(book))
                .exchange()
                .expectStatus().isCreated();
    }

}