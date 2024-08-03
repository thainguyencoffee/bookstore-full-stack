package com.bookstore.backend.book;

import com.bookstore.backend.IntegrationTestsBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

public class CategoryIntegrationTests extends IntegrationTestsBase {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void whenUnauthenticatedGetCategoriesThenOK() {
        webTestClient.get()
                .uri("/api/categories")
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void whenUnauthenticatedGetBooksOfCategoryThenOK() {
        List<String> expectedIsbn = List.of("5936095279", "2339238264", "4968497081", "9153338379", "5111638451");
        webTestClient.get()
                .uri("/api/categories/" + 10 + "/books")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$[*].isbn").value(containsInAnyOrder(expectedIsbn.toArray()))
                .jsonPath("$", hasSize(5));
    }

    @Test
    void whenUnauthenticatedGetBooksOfCategoryHasPagingThenOK() {
        webTestClient.get()
                .uri("/api/categories/" + 10 + "/books?page=0&size=3")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$", hasSize(3));
    }

}
