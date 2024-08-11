package com.bookstore.resourceserver.book;

import com.bookstore.resourceserver.IntegrationTestsBase;
import com.bookstore.resourceserver.book.dto.book.BookMetadataRequestDto;
import com.bookstore.resourceserver.book.dto.book.BookMetadataUpdateDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

// Summary of the test data
// Total categories: 7 [id] {'10000', '10001', '10002', '10003', '10004', '10005', '10006'}
// Total books: 2 [isbn]{'1234567890', '1234567891'}
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
                .jsonPath("$.content").isArray()
                .jsonPath("$.content.length()").isEqualTo(2);
    }

    @Test
    void whenUnauthenticatedAndBookAvailableGetBookByIsbnThenOk() {
        var isbn = "1234567890";
        webTestClient
                .get().uri("/api/books/" + isbn)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.isbn").isEqualTo(isbn)
                .jsonPath("$.title").isEqualTo("Spring boot in action")
                .jsonPath("$.author").isEqualTo("Craig Walls")
                .jsonPath("$.publisher").isEqualTo("Manning")
                .jsonPath("$.supplier").isEqualTo("Manning")
                .jsonPath("$.price").isEqualTo(1000000)
                .jsonPath("$.language").isEqualTo("ENGLISH")
                .jsonPath("$.coverType").isEqualTo("PAPERBACK")
                .jsonPath("$.numberOfPages").isEqualTo(300)
                .jsonPath("$.purchases").isEqualTo(10)
                .jsonPath("$.inventory").isEqualTo(100)
                .jsonPath("$.description").isEqualTo("Spring boot in action")
                .jsonPath("$.measure.width").isEqualTo(300)
                .jsonPath("$.measure.height").isEqualTo(400)
                .jsonPath("$.measure.thickness").isEqualTo(100)
                .jsonPath("$.measure.weight").isEqualTo(170);
    }

    @Test
    void whenUnauthenticatedAndBookNotAvailableGetBookByIsbnThenNotFound() {
        var isbn = "99999999999";
        webTestClient
                .get().uri("/api/books/" + isbn)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenUnauthenticatedCreateBookThen401() {
        var springCategory = 10002L;
        var bookReq = buildBookMetadata(true, "1234567892", springCategory);
        webTestClient
                .post()
                .uri("/api/books")
                .body(BodyInserters.fromValue(bookReq))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void whenAuthenticatedWithInvalidRoleCreateBookThen403() {
        var springCategory = 10002L;
        var bookReq = buildBookMetadata(true, "1234567892", springCategory);
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
        var springCategory = 10002L;
        var bookReq = buildBookMetadata(true, "1234567892", springCategory);
        webTestClient
                .post()
                .uri("/api/books")
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(bookReq))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.category.categoryId").isEqualTo(springCategory)
                .jsonPath("$.isbn").isEqualTo("1234567892")
                .jsonPath("$.title").isEqualTo(bookReq.getTitle())
                .jsonPath("$.author").isEqualTo(bookReq.getAuthor())
                .jsonPath("$.publisher").isEqualTo(bookReq.getPublisher())
                .jsonPath("$.supplier").isEqualTo(bookReq.getSupplier())
                .jsonPath("$.price").isEqualTo(bookReq.getPrice())
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
        var springCategory = 10002L;
        var bookReq = buildBookMetadata(false, "1234567892", springCategory);
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
        bookReq.setCategoryId(10002L);
        var isbn = "1234567891";
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
        bookReq.setCategoryId(10002L);
        var isbn = "1234567891";
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
        bookReq.setCategoryId(10002L);
        var isbn = "1234567891";
        webTestClient
                .patch()
                .uri("/api/books/" + isbn)
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(bookReq))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.category.categoryId").isEqualTo(10002L)
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
        bookReq.setWidth(0.0);
        var isbn = "1234567891";
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
        var isbn = "1234567890";
        webTestClient
                .delete()
                .uri("/api/books/" + isbn)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void whenAuthenticatedWithInvalidRoleDeleteBookThen403() {
        var isbn = "1234567890";
        webTestClient
                .delete()
                .uri("/api/books/" + isbn)
                .headers(headers -> headers.setBearerAuth(customerToken.getAccessToken()))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void whenAuthenticatedWithValidRoleDeleteBookThen204() {
        var isbn = "1234567890";
        webTestClient
                .delete()
                .uri("/api/books/" + isbn)
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void whenUnauthenticatedUploadThumbnailsBookThen401() {
        String isbn = "1234567890";
        webTestClient.post()
                .uri("/api/books/" + isbn + "/thumbnails")
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
                .uri("/api/books/" + isbn + "/thumbnails")
                .headers(headers -> headers.setBearerAuth(customerToken.getAccessToken()))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters
                        .fromMultipartData("thumbnails", new ClassPathResource("thumbnail.svg"))
                        .with("thumbnails", new ClassPathResource("thumbnail.svg")))
                .exchange()
                .expectStatus().isForbidden();
    }


    @Test
    void whenAuthenticatedWithValidRoleUpdateThumbnailsBookThenOK() throws com.fasterxml.jackson.core.JsonProcessingException {
        String isbn = "1234567890";

        List<String> thumbnails = uploadThumbnails(isbn);

        var req = new BookMetadataUpdateDto();
        req.setThumbnails(thumbnails);
        webTestClient.patch()
                .uri("/api/books/" + isbn)
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(req))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.thumbnails").isArray()
                .jsonPath("$.thumbnails.length()").isEqualTo(2);
    }

    @Test
    void whenAuthenticatedWithValidRoleUpdateThumbnailsBookAndRemoveOldThumbnailsThenOK() throws com.fasterxml.jackson.core.JsonProcessingException {
        String isbn = "1234567890";

        List<String> thumbnails = uploadThumbnails(isbn);

        var req = new BookMetadataUpdateDto();
        req.setThumbnails(thumbnails);
        webTestClient.patch()
                .uri("/api/books/" + isbn)
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
                .uri("/api/books/" + isbn)
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
                .uri("/api/books/" + isbn + "/thumbnails")
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
        return objectMapper.readValue(responseRaw, new TypeReference<>() {
        });
    }

    @Test
    void whenUnAuthenticatedGetBestSellersThenReturnOK() {
        Instant from = LocalDate.now()
                .withDayOfMonth(1)
                .minusMonths(1)
                .atStartOfDay().toInstant(ZoneOffset.UTC);
        webTestClient
                .get().uri(uriBuilder -> uriBuilder
                        .path("/api/books/best-sellers")
                        .queryParam("from", from)
                        .queryParam("page", 0)
                        .queryParam("size", 10).build()
                ).exchange()
                .expectStatus().isOk();
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