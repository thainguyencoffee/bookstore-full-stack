package com.bookstore.backend.book;

import com.bookstore.backend.IntegrationTestsBase;
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

        List<String> expectedIsbn = List.of("6954086462",
                "5274133268",
                "4187247453",
                "1034372457",
                "5018958839",
                "1868154635",
                "7498173758",
                "6424132410",
                "4899895256",
                "4172782186",
                "1450078000",
                "7357994445",
                "7237004293",
                "1765764446",
                "3749983842",
                "7343071939");
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
