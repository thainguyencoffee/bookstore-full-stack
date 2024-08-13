package com.bookstore.resourceserver.book;

import com.bookstore.resourceserver.IntegrationTestsBase;
import com.bookstore.resourceserver.book.author.Author;
import com.bookstore.resourceserver.book.category.Category;
import com.bookstore.resourceserver.book.dto.book.BookRequestDto;
import com.bookstore.resourceserver.book.dto.book.BookUpdateDto;
import com.bookstore.resourceserver.book.valuetype.Language;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.List;
import java.util.Set;

class BookIntegrationTests extends IntegrationTestsBase {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void whenUnauthenticatedGetAllBooksThenOk() {
        webTestClient
                .get().uri("/books")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray();
    }

    @ParameterizedTest
    @MethodSource("provideBooks")
    void whenUnauthenticatedAndBookAvailableGetBookByIsbnThenOk(Book book) {
        webTestClient
                .get().uri("/books/" + book.getIsbn())
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.isbn").isEqualTo(book.getIsbn())
                .jsonPath("$.title").isEqualTo(book.getTitle())
                .jsonPath("$.edition").isEqualTo(book.getEdition())
                .jsonPath("$.authors").isArray()
                .jsonPath("$.publisher").isEqualTo(book.getPublisher())
                .jsonPath("$.supplier").isEqualTo(book.getSupplier())
                .jsonPath("$.language").isEqualTo(book.getLanguage().toString())
                .jsonPath("$.description").isEqualTo(book.getDescription())
                .jsonPath("$.category.categoryId").isEqualTo(book.getCategory().getCategoryId())
                .jsonPath("$.category.categoryName").isEqualTo(book.getCategory().getCategoryName());
    }

    @Test
    void whenUnauthenticatedAndBookNotAvailableGetBookByIsbnThenNotFound() {
        var isbn = "99999999999";
        webTestClient
                .get().uri("/books/" + isbn)
                .exchange()
                .expectStatus().isNotFound();
    }

    @ParameterizedTest
    @MethodSource("provideSingleAuthorAndCategory")
    void whenUnauthenticatedCreateBookThen401(Author author, Category category) {
        var bookReq = buildBookMetadata(true, "1234567892", category.getId(), author.getId());
        webTestClient
                .post()
                .uri("/books")
                .body(BodyInserters.fromValue(bookReq))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @ParameterizedTest
    @MethodSource("provideSingleAuthorAndCategory")
    void whenAuthenticatedWithInvalidRoleCreateBookThen403(Author author, Category category) {
        var bookReq = buildBookMetadata(true, "1234567893", category.getId(), author.getId());
        webTestClient
                .post()
                .uri("/books")
                .headers(headers -> headers.setBearerAuth(customerToken.getAccessToken()))
                .body(BodyInserters.fromValue(bookReq))
                .exchange()
                .expectStatus().isForbidden();
    }


    @ParameterizedTest
    @MethodSource("provideSingleAuthorAndCategory")
    void whenAuthenticatedWithValidRoleCreateBookThen201(Author author, Category category) {
        var isbn = generatedNumbers10Digits();
        var bookReq = buildBookMetadata(true, isbn, category.getId(), author.getId());
        webTestClient
                .post()
                .uri("/books")
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(bookReq))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.category.categoryId").isEqualTo(category.getId())
                .jsonPath("$.isbn").isEqualTo(isbn)
                .jsonPath("$.title").isEqualTo(bookReq.getTitle())
                .jsonPath("$.authors[0].author").isEqualTo(author.getId())
                .jsonPath("$.authors[0].authorName").isEqualTo(author.getUserInformation().getFullName())
                .jsonPath("$.publisher").isEqualTo(bookReq.getPublisher())
                .jsonPath("$.supplier").isEqualTo(bookReq.getSupplier())
                .jsonPath("$.language").isEqualTo(bookReq.getLanguage().name())
                .jsonPath("$.description").isEqualTo(bookReq.getDescription())
                .jsonPath("$.edition").isEqualTo(bookReq.getEdition());
    }

    @ParameterizedTest
    @MethodSource("provideSingleAuthorAndCategoryAndBook")
    void whenAuthenticatedWithValidRoleCreateBookInvalidThen400(Author author, Category category) {
        var bookReq = buildBookMetadata(false, "1234567892", category.getId(), author.getId());
        webTestClient
                .post()
                .uri("/books")
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(bookReq))
                .exchange()
                .expectStatus().isBadRequest();
    }


    @Test
    void whenUnauthenticatedUpdateBookThen401() {
        var bookReq = new BookRequestDto();
        bookReq.setCategoryId(10002L);
        var isbn = "1234567891";
        webTestClient
                .patch()
                .uri("/books" + isbn)
                .body(BodyInserters.fromValue(bookReq))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void whenAuthenticatedWithInvalidRoleUpdateBookThen403() {
        var bookReq = new BookRequestDto();
        bookReq.setCategoryId(10002L);
        var isbn = "1234567891";
        webTestClient
                .patch()
                .uri("/books" + isbn)
                .headers(headers -> headers.setBearerAuth(customerToken.getAccessToken()))
                .body(BodyInserters.fromValue(bookReq))
                .exchange()
                .expectStatus().isForbidden();
    }

    @ParameterizedTest
    @MethodSource("provideSingleAuthorAndCategoryAndBook")
    void whenAuthenticatedWithValidRoleUpdateBookThen200(Author author, Category category, Book book) {
        var bookReq = new BookUpdateDto();
        bookReq.setLanguage(Language.ENGLISH);
        bookReq.setPublisher("updated publisher");
        bookReq.setSupplier("updated supplier");
        bookReq.setTitle("updated title " + book.getIsbn());
        bookReq.setDescription("updated description");
        bookReq.setEdition(3);
        Set<Long> authorIds = Set.of(author.getId());
        bookReq.setAuthorIds(authorIds);
        bookReq.setCategoryId(category.getId());

        webTestClient
                .patch()
                .uri("/books/" + book.getIsbn())
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(bookReq))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.isbn").isNotEmpty()
                .jsonPath("$.title").isEqualTo(bookReq.getTitle())
                .jsonPath("$.authors").isArray()
                .jsonPath("$.category").isNotEmpty()
                .jsonPath("$.publisher").isEqualTo(bookReq.getPublisher())
                .jsonPath("$.supplier").isEqualTo(bookReq.getSupplier())
                .jsonPath("$.language").isEqualTo(bookReq.getLanguage().name())
                .jsonPath("$.description").isEqualTo(bookReq.getDescription())
                .jsonPath("$.edition").isEqualTo(bookReq.getEdition());
    }

    @ParameterizedTest
    @MethodSource("provideSingleBook")
    void whenAuthenticatedWithValidRoleUpdateBookInvalidThen400(Book book) {
        var bookReq = new BookUpdateDto();
        var isbn = book.getIsbn();
        bookReq.setTitle("In Build a Large Language Model (from Scratch), you’ll discover how LLMs work from the inside out. In this insightful book, bestselling author Sebastian Raschka guides you step by step through creating your own LLM, explaining each stage with clear text, diagrams, and examples. You’ll go from the initial design and creation to pretraining on a general corpus, all the way to finetuning for specific tasks.\n" +
                "Build a Large Language Model (from Scratch) teaches you how to:\n" +
                "Plan and code all the parts of an LLM\n" +
                "Prepare a dataset suitable for LLM training\n" +
                "Finetune LLMs for text classification and with your own data\n" +
                "Apply instruction tuning techniques to ensure your LLM follows instructions\n" +
                "Load pretrained weights into an LLM\n" +
                "The large language models (LLMs) that power cutting-edge AI tools like ChatGPT, Bard, and Copilot seem like a miracle, but they’re not magic. This book demystifies LLMs by helping you build your own from scratch. You’ll get a unique and valuable insight into how LLMs work, learn how to evaluate their quality, and pick up concrete techniques to finetune and improve them.\n" +
                "The process you use to train and develop your own small-but-functional model in this book follows the same steps used to deliver huge-scale foundation models like GPT-4. Your small-scale LLM can be developed on an ordinary laptop, and you’ll be able to use it as your own personal assistant.");
        webTestClient
                .patch()
                .uri("/books/" + isbn)
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(bookReq))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void whenUnauthenticatedDeleteBookThen401() {
        var isbn = "1234567890";
        webTestClient
                .delete()
                .uri("/books/" + isbn)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void whenAuthenticatedWithInvalidRoleDeleteBookThen403() {
        var isbn = "1234567890";
        webTestClient
                .delete()
                .uri("/books/" + isbn)
                .headers(headers -> headers.setBearerAuth(customerToken.getAccessToken()))
                .exchange()
                .expectStatus().isForbidden();
    }

    @ParameterizedTest
    @MethodSource("provideSingleBook")
    void whenAuthenticatedWithValidRoleDeleteBookThen204(Book book) {
        webTestClient
                .delete()
                .uri("/books/" + book.getIsbn())
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void whenUnauthenticatedUploadThumbnailsBookThen401() {
        String isbn = "1234567890";
        webTestClient.post()
                .uri("/books/" + isbn + "/thumbnails")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters
                        .fromMultipartData("thumbnails", new ClassPathResource("thumbnail.svg"))
                        .with("thumbnails", new ClassPathResource("thumbnail.svg")))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void whenAuthenticatedWithInvalidRoleUploadThumbnailsBookThen403() {
        String isbn = "1234567890";
        webTestClient.post()
                .uri("/books/" + isbn + "/thumbnails")
                .headers(headers -> headers.setBearerAuth(customerToken.getAccessToken()))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters
                        .fromMultipartData("thumbnails", new ClassPathResource("thumbnail.svg"))
                        .with("thumbnails", new ClassPathResource("thumbnail.svg")))
                .exchange()
                .expectStatus().isForbidden();
    }


    @ParameterizedTest
    @MethodSource("provideSingleBook")
    void whenAuthenticatedWithValidRoleUpdateThumbnailsBookThenOK(Book book) throws com.fasterxml.jackson.core.JsonProcessingException {
        String isbn = book.getIsbn();

        List<String> thumbnails = uploadThumbnails(isbn);

        var req = new BookUpdateDto();
        req.setThumbnails(thumbnails);
        webTestClient.patch()
                .uri("/books/" + isbn)
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(req))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.thumbnails").isArray()
                .jsonPath("$.thumbnails.length()").isEqualTo(2);
    }

    @ParameterizedTest
    @MethodSource("provideSingleBook")
    void whenAuthenticatedWithValidRoleUpdateThumbnailsBookAndRemoveOldThumbnailsThenOK(Book book) throws com.fasterxml.jackson.core.JsonProcessingException {
        String isbn = book.getIsbn();

        List<String> thumbnails = uploadThumbnails(isbn);

        var req = new BookUpdateDto();
        req.setThumbnails(thumbnails);
        webTestClient.patch()
                .uri("/books/" + isbn)
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(req))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.thumbnails").isArray()
                .jsonPath("$.thumbnails.length()").isEqualTo(2);

        List<String> thumbnails2 = uploadThumbnails(isbn);
        thumbnails2.add(thumbnails.get(0));
        req.setThumbnails(thumbnails2);
        webTestClient.patch()
                .uri("/books/" + isbn)
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(req))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.thumbnails").isArray()
                .jsonPath("$.thumbnails.length()").isEqualTo(3);

        // Phía digital ocean ở thư mục thumbnails của book này nên chỉ có 3 files
    }

    private List<String> uploadThumbnails(String isbn) throws com.fasterxml.jackson.core.JsonProcessingException {
        String responseRaw = webTestClient.post()
                .uri("/books/" + isbn + "/thumbnails")
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters
                        .fromMultipartData("thumbnails", new ClassPathResource("thumbnail.svg"))
                        .with("thumbnails", new ClassPathResource("thumbnail.svg")))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult().getResponseBody();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(responseRaw, new TypeReference<>() {});
    }

    private static BookRequestDto buildBookMetadata(boolean isValid, String isbn, Long categoryId, Long authorId) {
        var bookDto = new BookRequestDto();
        bookDto.setCategoryId(categoryId);
        bookDto.setAuthorIds(Set.of(authorId));
        if (isValid) {
            bookDto.setIsbn(isbn);
            bookDto.setTitle("DEMO_TITLE");
            bookDto.setPublisher("DEMO_PUBLISHER");
            bookDto.setSupplier("DEMO_SUPPLIER");
            bookDto.setLanguage(Language.VIETNAMESE);
            bookDto.setDescription("DEMO_DESCRIPTION");
            bookDto.setEdition(2);
        } else {
            bookDto.setIsbn(isbn + "1234");
            bookDto.setTitle("");
            bookDto.setPublisher("");
            bookDto.setSupplier("");
            bookDto.setLanguage(Language.VIETNAMESE);
            bookDto.setDescription("");
            bookDto.setEdition(2);
        }
        return bookDto;
    }

    private String generatedNumbers10Digits() {
        var number = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
        return String.valueOf(number);
    }

}