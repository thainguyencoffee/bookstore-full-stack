package com.bookstorefullstack.bookstore;

import com.bookstorefullstack.bookstore.author.Author;
import com.bookstorefullstack.bookstore.author.AuthorRepository;
import com.bookstorefullstack.bookstore.book.Book;
import com.bookstorefullstack.bookstore.book.BookRepository;
import com.bookstorefullstack.bookstore.category.Category;
import com.bookstorefullstack.bookstore.category.CategoryRepository;
import com.bookstorefullstack.bookstore.emailpreference.EmailPreferencesRepository;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.google.common.net.HttpHeaders;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

@AutoConfigureWebTestClient(timeout = "36000")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class IntegrationTestsBase {

    protected static KeycloakToken employeeToken;
    protected static KeycloakToken customerToken;
    @Autowired
    protected CategoryRepository categoryRepository;
    @Autowired
    protected BookRepository bookRepository;
    @Autowired
    protected AuthorRepository authorRepository;
    @Autowired
    protected EmailPreferencesRepository emailPreferencesRepository;

    @DynamicPropertySource
    static void keycloakProperties(DynamicPropertyRegistry registry) {
        KeycloakTestContainer.keycloakProperties(registry);
    }

    @BeforeAll
    static void generateAccessToken() {
        WebClient webClient = WebClient.builder()
                .baseUrl(KeycloakTestContainer.getInstance().getAuthServerUrl() +
                        "/realms/bookstore/protocol/openid-connect/token")
                .defaultHeader(HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
        employeeToken = authenticateWith("employee", "1", webClient);
        customerToken = authenticateWith("user", "1", webClient);
    }

    protected static class KeycloakToken {
        private final String accessToken;

        @JsonCreator
        private KeycloakToken(@JsonProperty("access_token") final String accessToken) {
            this.accessToken = accessToken;
        }

        public String getAccessToken() {
            return accessToken;
        }
    }

    private static KeycloakToken authenticateWith(
            String username, String password, WebClient webClient) {
        return webClient
                .post()
                .body(BodyInserters.fromFormData("grant_type", "password")
                        .with("client_id", "bff-client")
                        .with("client_secret", "secret")
                        .with("username", username)
                        .with("password", password)
                )
                .retrieve()
                .bodyToMono(KeycloakToken.class)
                .block();
    }

    protected Stream<Arguments> provideSingleBook() {
        Random random = new Random();
        List<Book> all = bookRepository.findAll();
        int i = random.nextInt(all.size());
        return Stream.of(Arguments.of(all.get(i)));
    }

    /*Combination*/
    protected Stream<Arguments> provideSingleAuthorAndCategory() {
        Random random = new Random();
        List<Author> authors = authorRepository.findAll();
        List<Category> categories = categoryRepository.findAll();
        int i = random.nextInt(authors.size());
        int j = random.nextInt(categories.size());
        return Stream.of(Arguments.of(authors.get(i), categories.get(j)));
    }

    protected Stream<Arguments> provideSingleAuthorAndCategoryAndBook() {
        Random random = new Random();
        List<Author> authors = authorRepository.findAll();
        List<Category> categories = categoryRepository.findAll();
        List<Book> books = bookRepository.findAll();
        int i = random.nextInt(authors.size());
        int j = random.nextInt(categories.size());
        int k = random.nextInt(books.size());
        return Stream.of(Arguments.of(authors.get(i), categories.get(j), books.get(k)));
    }

}