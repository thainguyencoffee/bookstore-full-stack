package com.bookstore.resourceserver.book;

import com.bookstore.resourceserver.IntegrationTestsBase;
import com.bookstore.resourceserver.book.category.Category;
import com.bookstore.resourceserver.book.dto.category.CategoryMetadataRequestDto;
import com.bookstore.resourceserver.book.dto.category.CategoryMetadataUpdateDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;

public class CategoryIntegrationTests extends IntegrationTestsBase {

    @Autowired
    private WebTestClient webTestClient;

    /*Categories*/
    private Stream<Arguments> provideCategories() {
        return categoryRepository.findAll().stream().map(Arguments::of);
    }

    private Stream<Arguments> provideCategoryAndChildren() {
        Random random = new Random();
        List<Category> all = categoryRepository.findAll();
        int i = random.nextInt(all.size());
        Category category = all.get(i);
        return Stream.of(Arguments.of(category, categoryRepository.findAllSubCategoriesById(category.getId())));
    }

    private Stream<Arguments> provideSingleCategory() {
        Random random = new Random();
        List<Category> all = categoryRepository.findAll();
        int i = random.nextInt(all.size());
        return Stream.of(Arguments.of(all.get(i)));
    }

    private Stream<Arguments> provideSingleCategoryAndBooks() {
        Random random = new Random();
        List<Category> all = categoryRepository.findAll();
        int i = random.nextInt(all.size());
        Category category = all.get(i);
        return Stream.of(Arguments.of(category, bookRepository.findAllByCategoryId(category.getId(), 20, 0)));
    }

    @Test
    void test() {
        System.out.println(customerToken.getAccessToken());
    }

    @Test
    void whenUnauthenticatedGetCategoriesThenOK() {
        webTestClient.get()
                .uri("/categories")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$").isArray();
    }

    @ParameterizedTest
    @MethodSource("provideSingleCategoryAndBooks")
    void whenUnauthenticatedGetBooksOfCategoryThenOK(Category category, List<Book> books) {
        webTestClient.get()
                .uri("/categories/" + category.getId() + "/books")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$", hasSize(books.size()));
    }

    @ParameterizedTest
    @MethodSource("provideCategoryAndChildren")
    void whenUnauthenticatedGetChildCategoryOfACategoryThenOK(Category category, List<Category> children) {
        webTestClient.get()
                .uri("/categories/" + category.getId() + "/children")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$", hasSize(children.size()));
    }

    @ParameterizedTest
    @MethodSource("provideSingleCategory")
    void whenUnauthenticatedGetCategoryByIdThenOK(Category category) {
        webTestClient.get()
                .uri("/categories/" + category.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(category.getId())
                .jsonPath("$.name").isEqualTo(category.getName())
                .jsonPath("$.thumbnail").isEqualTo(category.getThumbnail());
    }

    @Test
    void whenUnauthenticatedGetCategoryByIdNotFoundThen404() {
        webTestClient.get()
                .uri("/categories/" + 999999)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenUnauthenticatedCreateCategoryThen401() {
        var requestBody = buildCategoryMetadata("Demo name", 10000L);

        webTestClient.post()
                .uri("/categories")
                .body(BodyInserters.fromValue(requestBody))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void whenAuthenticatedWithInvalidRoleCreateCategoryThen403() {
        var requestBody = buildCategoryMetadata("Demo name", 10000L);

        webTestClient.post()
                .uri("/categories")
                .body(BodyInserters.fromValue(requestBody))
                .headers(headers -> headers.setBearerAuth(customerToken.getAccessToken()))
                .exchange()
                .expectStatus().isForbidden();
    }

    @ParameterizedTest
    @MethodSource("provideSingleCategory")
    void whenAuthenticatedWithValidRoleCreateCategoryThen201(Category category) {
        var requestBody = buildCategoryMetadata("Demo name", category.getId());

        webTestClient.post()
                .uri("/categories")
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
                .uri("/categories")
                .body(BodyInserters.fromValue(requestBody))
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenUnauthenticatedUpdateCategoryThen401() {
        var requestBody = new CategoryMetadataUpdateDto("Category name updated", null);

        webTestClient.patch()
                .uri("/categories/" + 10000L)
                .body(BodyInserters.fromValue(requestBody))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void whenAuthenticatedWithInvalidRoleUpdateCategoryThen403() {
        var requestBody = new CategoryMetadataUpdateDto("Category name updated", null);

        webTestClient.patch()
                .uri("/categories/" + 10000L)
                .body(BodyInserters.fromValue(requestBody))
                .headers(headers -> headers.setBearerAuth(customerToken.getAccessToken()))
                .exchange()
                .expectStatus().isForbidden();
    }

    @ParameterizedTest
    @MethodSource("provideSingleCategory")
    void whenAuthenticatedWithValidRoleUpdateCategoryThen200(Category category) {
        var requestBody = new CategoryMetadataUpdateDto("Category name updated", null);

        webTestClient.patch()
                .uri("/categories/" + category.getId())
                .body(BodyInserters.fromValue(requestBody))
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(category.getId())
                .jsonPath("$.name").isEqualTo(requestBody.getName())
                .jsonPath("$.parentId").isEqualTo(category.getParentId())
                .jsonPath("$.thumbnail").isEqualTo(category.getThumbnail());
    }

    @Test
    void whenUnauthenticatedDeleteCategoryThen401() {
        webTestClient.delete()
                .uri("/categories/" + 10001)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void whenAuthenticatedWithInvalidRoleDeleteCategoryThen403() {
        webTestClient.delete()
                .uri("/categories/" + 10001)
                .headers(headers -> headers.setBearerAuth(customerToken.getAccessToken()))
                .exchange()
                .expectStatus().isForbidden();
    }
    
    @ParameterizedTest
    @MethodSource("provideSingleCategory")
    void whenAuthenticatedWithValidRoleDeleteCategoryThen204(Category category) {
        webTestClient.delete()
                .uri("/categories/" + category.getId())
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void whenAuthenticatedWithValidRoleDeleteCategoryNotFoundThen404() {
        webTestClient.delete()
                .uri("/categories/" + 9999)
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .exchange()
                .expectStatus().isNotFound();
    }

    @ParameterizedTest
    @MethodSource("provideSingleCategory")
    void whenUnauthenticatedUploadThumbnailsThen401(Category category) {
        webTestClient.post()
                .uri("/categories/" + category.getId() + "/thumbnails")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("thumbnail", new ClassPathResource("thumbnail.svg")))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @ParameterizedTest
    @MethodSource("provideSingleCategory")
    void whenAuthenticatedWithInvalidRoleUploadThumbnailsThen403(Category category) {
        webTestClient.post()
                .uri("/categories/" + category.getId() + "/thumbnails")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("thumbnail", new ClassPathResource("thumbnail.svg")))
                .headers(headers -> headers.setBearerAuth(customerToken.getAccessToken()))
                .exchange()
                .expectStatus().isForbidden();
    }

    @ParameterizedTest
    @MethodSource("provideSingleCategory")
    void whenAuthenticatedWithValidRoleUpdateThumbnailsBookAndRemoveOldThumbnailsThenOK(Category category) throws com.fasterxml.jackson.core.JsonProcessingException {
        var id = category.getId();
        String thumbnails = uploadThumbnails(id);

        var req = new CategoryMetadataUpdateDto();
        req.setThumbnail(thumbnails);
        webTestClient.patch()
                .uri("/categories/" + id)
                .headers(headers -> headers.setBearerAuth(employeeToken.getAccessToken()))
                .body(BodyInserters.fromValue(req))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.thumbnail").isNotEmpty();

        String thumbnail2 = uploadThumbnails(id);
        req.setThumbnail(thumbnail2);
        webTestClient.patch()
                .uri("/categories/" + id)
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
                .uri("/categories/" + id + "/thumbnails")
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
