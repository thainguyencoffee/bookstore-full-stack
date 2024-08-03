package com.bookstore.backend.book;

import com.bookstore.backend.IntegrationTestsBase;
import com.bookstore.backend.book.dto.BookMetadataRequestDto;
import com.bookstore.backend.book.dto.BookMetadataUpdateDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

class BookIntegrationTests extends IntegrationTestsBase {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void whenUnauthenticatedGetAllBooksThenOk() {
        webTestClient
                .get().uri("/api/books")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray();
    }

    @Test
    void whenUnauthenticatedAndBookAvailableGetBookByIsbnThenOk() {
        // from classpath:data.sql -> books available
        var isbn = "5936095279";
        webTestClient
                .get().uri("/api/books/" + isbn)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.isbn").isEqualTo(isbn);
    }

    @Test
    void whenUnauthenticatedAndBookNotAvailableGetBookByIsbnThenNotFound() {
        // from classpath:data.sql -> books available
        var isbn = "99999999999";
        webTestClient
                .get().uri("/api/books/" + isbn)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenUnauthenticatedCreateBookThen401() {
        var categoryIdAvailable = 6L;
        var bookReq = buildBookMetadata(true, "1234567890", categoryIdAvailable);
        webTestClient
                .post()
                .uri("/api/books")
                .body(BodyInserters.fromValue(bookReq))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void whenAuthenticatedWithInvalidRoleCreateBookThen403() {
        var categoryIdAvailable = 6L;
        var bookReq = buildBookMetadata(true, "1234567890", categoryIdAvailable);
        webTestClient
                .post()
                .uri("/api/books")
                .headers(headers -> headers.setBearerAuth(customerToken.getAccessToken()))
                .body(BodyInserters.fromValue(bookReq))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void whenAuthenticatedWithValidRoleCreateBookThen201() {
        var categoryIdAvailable = 6L;
        var isbn = "1234567890";
        var bookReq = buildBookMetadata(true, isbn, categoryIdAvailable);
        webTestClient
                .post()
                .uri("/api/books")
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(bookReq))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.category.categoryId").isEqualTo(categoryIdAvailable)
                .jsonPath("$.isbn").isEqualTo(isbn)
                .jsonPath("$.title").isEqualTo(bookReq.getTitle())
                .jsonPath("$.author").isEqualTo(bookReq.getAuthor())
                .jsonPath("$.publisher").isEqualTo(bookReq.getPublisher())
                .jsonPath("$.supplier").isEqualTo(bookReq.getSupplier())
                .jsonPath("$.language").isEqualTo(bookReq.getLanguage().name())
                .jsonPath("$.coverType").isEqualTo(bookReq.getCoverType().name())
                .jsonPath("$.numberOfPages").isEqualTo(bookReq.getNumberOfPages())
                .jsonPath("$.purchases").isEqualTo(bookReq.getPurchases())
                .jsonPath("$.inventory").isEqualTo(bookReq.getInventory())
                .jsonPath("$.description").isEqualTo(bookReq.getDescription())
                .jsonPath("$.measure.width").isEqualTo(bookReq.getWidth())
                .jsonPath("$.measure.height").isEqualTo(bookReq.getHeight())
                .jsonPath("$.measure.thickness").isEqualTo(bookReq.getThickness())
                .jsonPath("$.measure.weight").isEqualTo(bookReq.getWeight());
    }

    @Test
    void whenAuthenticatedWithValidRoleCreateBookInvalidThen400() {
        var categoryIdAvailable = 6L;
        var isbn = "1234567890";
        var bookReq = buildBookMetadata(false, isbn, categoryIdAvailable);
        webTestClient
                .post()
                .uri("/api/books")
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(bookReq))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void whenUnauthenticatedUpdateBookThen401() {
        var bookReq = new BookMetadataRequestDto();
        bookReq.setCategoryId(4L);
        var isbn = "5936095279";
        webTestClient
                .patch()
                .uri("/api/books" + isbn)
                .body(BodyInserters.fromValue(bookReq))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void whenAuthenticatedWithInvalidRoleUpdateBookThen403() {
        var bookReq = new BookMetadataRequestDto();
        bookReq.setCategoryId(4L);
        var isbn = "5936095279";
        webTestClient
                .patch()
                .uri("/api/books" + isbn)
                .headers(headers -> headers.setBearerAuth(customerToken.getAccessToken()))
                .body(BodyInserters.fromValue(bookReq))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void whenAuthenticatedWithValidRoleUpdateBookThen200() throws JsonProcessingException {
        var bookReq = new BookMetadataRequestDto();
        bookReq.setCategoryId(4L);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(bookReq);
        System.out.println(json);
        var isbn = "5936095279";
        webTestClient
                .patch()
                .uri("/api/books/" + isbn)
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(bookReq))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.category.categoryId").isEqualTo(4L)
                .jsonPath("$.isbn").isNotEmpty()
                .jsonPath("$.title").isNotEmpty()
                .jsonPath("$.author").isNotEmpty()
                .jsonPath("$.publisher").isNotEmpty()
                .jsonPath("$.supplier").isNotEmpty()
                .jsonPath("$.language").isNotEmpty()
                .jsonPath("$.coverType").isNotEmpty()
                .jsonPath("$.numberOfPages").isNotEmpty()
                .jsonPath("$.purchases").isNotEmpty()
                .jsonPath("$.inventory").isNotEmpty()
                .jsonPath("$.measure.width").isNotEmpty()
                .jsonPath("$.measure.height").isNotEmpty()
                .jsonPath("$.measure.thickness").isNotEmpty()
                .jsonPath("$.measure.weight").isNotEmpty();
    }

    @Test
    void whenAuthenticatedWithValidRoleUpdateBookInvalidThen400() {
        var bookReq = new BookMetadataUpdateDto();
        bookReq.setCategoryId(4L);
        bookReq.setWidth(0.0);
        var isbn = "5936095279";
        webTestClient
                .patch()
                .uri("/api/books/" + isbn)
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(bookReq))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void whenUnauthenticatedDeleteBookThen401() {
        var isbn = "5936095279";
        webTestClient
                .delete()
                .uri("/api/books/" + isbn)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void whenAuthenticatedWithInvalidRoleDeleteBookThen403() {
        var isbn = "5936095279";
        webTestClient
                .delete()
                .uri("/api/books/" + isbn)
                .headers(headers -> headers.setBearerAuth(customerToken.getAccessToken()))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void whenAuthenticatedWithValidRoleDeleteBookThen204() {
        var isbn = "5936095279";
        webTestClient
                .delete()
                .uri("/api/books/" + isbn)
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void whenAuthenticatedWithValidRoleUploadThumbnailThen200() throws IOException {
        var isbn = "5936095279";

        MockMultipartFile thumbnailsAdd = new MockMultipartFile(
                "thumbnailsAdd",
                "test-thumbnail.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "lol".getBytes(StandardCharsets.UTF_8)
        );

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("thumbnailsChange", List.of(""));
        body.add("thumbnailsAdd", new ByteArrayResource(thumbnailsAdd.getBytes()) {
            @Override
            public String getFilename() {
                return thumbnailsAdd.getOriginalFilename();
            }
        });

        webTestClient
                .post()
                .uri("/api/books/" + isbn + "/upload-thumbnail")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(body))
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void whenAuthenticatedWithInValidRoleUploadThumbnailThen403() throws IOException {
        var isbn = "5936095279";

        MockMultipartFile thumbnailsAdd = new MockMultipartFile(
                "thumbnailsAdd",
                "test-thumbnail.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "lol".getBytes(StandardCharsets.UTF_8)
        );

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("thumbnailsChange", List.of(""));
        body.add("thumbnailsAdd", new ByteArrayResource(thumbnailsAdd.getBytes()) {
            @Override
            public String getFilename() {
                return thumbnailsAdd.getOriginalFilename();
            }
        });

        webTestClient
                .post()
                .uri("/api/books/" + isbn + "/upload-thumbnail")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(body))
                .headers(headers -> headers.setBearerAuth(customerToken.getAccessToken()))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void whenAuthenticatedWithValidRoleUploadEmptyThumbnailThen201() throws IOException {
        var isbn = "5936095279";

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("thumbnailsChange", "");

        webTestClient
                .post()
                .uri("/api/books/" + isbn + "/upload-thumbnail")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(body))
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.thumbnails").isArray()
                .jsonPath("$.thumbnails.length()").isEqualTo(0);
    }

    private static BookMetadataRequestDto buildBookMetadata(boolean isValid, String isbn, Long categoryId) {
        var bookDto = new BookMetadataRequestDto();
        bookDto.setIsbn(isbn);
        bookDto.setCategoryId(categoryId);
        if (isValid) {
            bookDto.setTitle("DEMO_TITLE");
            bookDto.setPrice(1000000L);
            bookDto.setAuthor("DEMO_AUTHOR");
            bookDto.setPublisher("DEMO_PUBLISHER");
            bookDto.setSupplier("DEMO_SUPPLIER");
            bookDto.setLanguage(Language.VIETNAMESE);
            bookDto.setCoverType(CoverType.PAPERBACK);
            bookDto.setNumberOfPages(1000);
            bookDto.setPurchases(10);
            bookDto.setInventory(100);
            bookDto.setDescription("DEMO_DESCRIPTION");
            bookDto.setWidth(300.0);
            bookDto.setHeight(400.0);
            bookDto.setThickness(100.0);
            bookDto.setWeight(170.0);
        } else {
            bookDto.setTitle("");
            bookDto.setPrice(0L);
            bookDto.setAuthor("");
            bookDto.setPublisher("");
            bookDto.setSupplier("");
            bookDto.setLanguage(Language.VIETNAMESE);
            bookDto.setCoverType(CoverType.PAPERBACK);
            bookDto.setNumberOfPages(0);
            bookDto.setPurchases(0);
            bookDto.setInventory(0);
            bookDto.setDescription("");
            bookDto.setWidth(0.0);
            bookDto.setHeight(0.0);
            bookDto.setThickness(0.0);
            bookDto.setWeight(0.0);
        }
        return bookDto;
    }

}