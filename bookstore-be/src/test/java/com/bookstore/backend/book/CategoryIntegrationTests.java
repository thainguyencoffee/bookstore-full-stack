package com.bookstore.backend.book;

import com.bookstore.backend.IntegrationTestsBase;
import com.bookstore.backend.book.dto.book.BookMetadataUpdateDto;
import com.bookstore.backend.book.dto.category.CategoryMetadataRequestDto;
import com.bookstore.backend.book.dto.category.CategoryMetadataUpdateDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

// Summary of the test data
// Total categories: 7 [id] {'10000', '10001', '10002', '10003', '10004', '10005', '10006'}
// Total books: 2 [isbn] {'1234567890', '1234567891'}
// Category: 10000 has no books and 2 children [10001, 10002]
// Category: 10001 has no books and 1 children [10002]
// Category: 10002 has 1 book [1234567890] and 0 children
// Category: 10003 has no books and 3 children [10004, 10005, 10006]
// Category: 10004 has no books and 2 children [10005, 10006]
// Category: 10005 has 1 book [1234567891] and 0 children
// Category: 10006 has no books and 0 children
public class CategoryIntegrationTests extends IntegrationTestsBase {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void whenUnauthenticatedGetCategoriesThenOK() {
        webTestClient.get()
                .uri("/api/categories")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(7);
    }

    @Test
    void whenUnauthenticatedGetBooksOfCategoryThenOK() {
        List<String> expectedIsbn = List.of("1234567890");
        var categoryId = 10002L;
        webTestClient.get()
                .uri("/api/categories/" + categoryId + "/books")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$[*].isbn").value(containsInAnyOrder(expectedIsbn.toArray()))
                .jsonPath("$", hasSize(1));
    }

    @Test
    void whenUnauthenticatedGetChildCategoryOfACategoryThenOK() {
        webTestClient.get()
                .uri("/api/categories/" + 10000 + "/children")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$[*].id").value(containsInAnyOrder(10001, 10002));
    }

    @Test
    void whenUnauthenticatedGetChildCategoryOfACategory2ThenOK() {
        webTestClient.get()
                .uri("/api/categories/" + 10003 + "/children")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$[*].id").value(containsInAnyOrder(10004, 10005, 10006));
    }

    @Test
    void whenUnauthenticatedGetCategoryByIdThenOK() {
        webTestClient.get()
                .uri("/api/categories/" + 10000)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(10000);
    }

    @Test
    void whenUnauthenticatedGetCategoryByIdNotFoundThen404() {
        webTestClient.get()
                .uri("/api/categories/" + 999999)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenUnauthenticatedCreateCategoryThen401() {
        var requestBody = buildCategoryMetadata("Demo name", 10000L);

        webTestClient.post()
                .uri("/api/categories")
                .body(BodyInserters.fromValue(requestBody))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void whenAuthenticatedWithInvalidRoleCreateCategoryThen403() {
        var requestBody = buildCategoryMetadata("Demo name", 10000L);

        webTestClient.post()
                .uri("/api/categories")
                .body(BodyInserters.fromValue(requestBody))
                .headers(headers -> headers.setBearerAuth(customerToken.getAccessToken()))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void whenAuthenticatedWithValidRoleCreateCategoryThen201() {
        var requestBody = buildCategoryMetadata("Demo name", 10000L);

        webTestClient.post()
                .uri("/api/categories")
                .body(BodyInserters.fromValue(requestBody))
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo(requestBody.getName())
                .jsonPath("$.parentId").isEqualTo(requestBody.getParentId());
    }

    @Test
    void whenAuthenticatedWithValidRoleCreateCategoryWithParentIdNotFoundThen404() {
        var requestBody = buildCategoryMetadata("Demo name", 9999L);

        webTestClient.post()
                .uri("/api/categories")
                .body(BodyInserters.fromValue(requestBody))
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenUnauthenticatedUpdateCategoryThen401() {
        var requestBody = new CategoryMetadataUpdateDto("Category name updated", null);

        webTestClient.patch()
                .uri("/api/categories/" + 10000L)
                .body(BodyInserters.fromValue(requestBody))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void whenAuthenticatedWithInvalidRoleUpdateCategoryThen403() {
        var requestBody = new CategoryMetadataUpdateDto("Category name updated", null);

        webTestClient.patch()
                .uri("/api/categories/" + 10000L)
                .body(BodyInserters.fromValue(requestBody))
                .headers(headers -> headers.setBearerAuth(customerToken.getAccessToken()))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void whenAuthenticatedWithValidRoleUpdateCategoryThen200() {
        var requestBody = new CategoryMetadataUpdateDto("Category name updated", null);

        webTestClient.patch()
                .uri("/api/categories/" + 10001)
                .body(BodyInserters.fromValue(requestBody))
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(10001)
                .jsonPath("$.name").isEqualTo(requestBody.getName())
                .jsonPath("$.parentId").isEqualTo(10000);
    }

    @Test
    void whenUnauthenticatedDeleteCategoryThen401() {
        webTestClient.delete()
                .uri("/api/categories/" + 10001)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void whenAuthenticatedWithInvalidRoleDeleteCategoryThen403() {
        webTestClient.delete()
                .uri("/api/categories/" + 10001)
                .headers(headers -> headers.setBearerAuth(customerToken.getAccessToken()))
                .exchange()
                .expectStatus().isForbidden();
    }
    
    @Test
    void whenAuthenticatedWithValidRoleDeleteCategoryThen204() {
        webTestClient.delete()
                .uri("/api/categories/" + 10001)
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void whenAuthenticatedWithValidRoleDeleteCategoryNotFoundThen404() {
        webTestClient.delete()
                .uri("/api/categories/" + 9999)
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenUnauthenticatedUploadThumbnailsThen401() {
        webTestClient.post()
                .uri("/api/categories/" + 10001 + "/thumbnails")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("thumbnail", new ClassPathResource("thumbnail.svg")))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void whenAuthenticatedWithInvalidRoleUploadThumbnailsThen403() {
        webTestClient.post()
                .uri("/api/categories/" + 10001 + "/thumbnails")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("thumbnail", new ClassPathResource("thumbnail.svg")))
                .headers(headers -> headers.setBearerAuth(customerToken.getAccessToken()))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void whenAuthenticatedWithValidRoleUpdateThumbnailsBookAndRemoveOldThumbnailsThenOK() throws com.fasterxml.jackson.core.JsonProcessingException {
        var id = 10001L;
        String thumbnails = uploadThumbnails(id);

        var req = new CategoryMetadataUpdateDto();
        req.setThumbnail(thumbnails);
        webTestClient.patch()
                .uri("/api/categories/" + 10001)
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(req))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.thumbnail").isNotEmpty();

        String thumbnail2 = uploadThumbnails(id);
        req.setThumbnail(thumbnail2);
        webTestClient.patch()
                .uri("/api/categories/" + 10001)
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(req))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.thumbnail").isNotEmpty();

        // Phía digital ocean ở thư mục thumbnail của category này nên chỉ có 1 files
    }

    private String uploadThumbnails(Long id) throws JsonProcessingException {
        return webTestClient.post()
                .uri("/api/categories/" + id + "/thumbnails")
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("thumbnail", new ClassPathResource("thumbnail.svg")))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult().getResponseBody();
    }

    private static CategoryMetadataRequestDto buildCategoryMetadata(String name, Long parent) {
        return new CategoryMetadataRequestDto(name, parent);
    }

}
