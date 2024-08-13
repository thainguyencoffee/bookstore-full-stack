package com.bookstore.resourceserver.book.emailpreferences;

import com.bookstore.resourceserver.IntegrationTestsBase;
import com.bookstore.resourceserver.book.category.Category;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class EmailPreferencesIntTests extends IntegrationTestsBase {

    @Autowired
    private WebTestClient webTestClient;

    private Stream<Arguments> provideSingleEmailPreferences() {
        List<EmailPreferences> emailPreferences = emailPreferencesRepository.findAll();
        Random random = new Random();
        return Stream.of(Arguments.of(emailPreferences.get(random.nextInt(emailPreferences.size()))));
    }

    private Stream<Arguments> provideSingleCategoryPreferences() {
        List<Category> categories = categoryRepository.findAll();
        Random random = new Random();
        return Stream.of(Arguments.of(categories.get(random.nextInt(categories.size()))));
    }

    @ParameterizedTest
    @MethodSource("provideSingleCategoryPreferences")
    void whenUnauthenticatedSubscribeEmailPreferencesWithValidDataThen201(Category category) {
        var dtoRequest = buildDto("demo@gmail.com", category.getId());

        webTestClient.post().uri("/email-preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(dtoRequest))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.email").isEqualTo(dtoRequest.getEmail())
                .jsonPath("$.id").isNotEmpty();
    }

    @ParameterizedTest
    @MethodSource("provideSingleCategoryPreferences")
    void whenUnauthenticatedSubscribeEmailPreferencesWithCategoryNotfoundThen404(Category category) {
        var dtoRequest = buildDto("demo@gmail.com", category.getId());

        dtoRequest.setCategoryIds(List.of(9999998L, 9999999L));
        webTestClient.post().uri("/email-preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(dtoRequest))
                .exchange()
                .expectStatus().isNotFound();
    }

    @ParameterizedTest
    @MethodSource("provideSingleCategoryPreferences")
    void whenUnauthenticatedSubscribeEmailPreferencesWithBadBodyThen400(Category category) {
        var dtoRequest = buildDto("demo@gmail.com", category.getId());

        dtoRequest.setEmail("");
        dtoRequest.setCategoryIds(List.of(category.getId()));
        webTestClient.post().uri("/email-preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(dtoRequest))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @ParameterizedTest
    @MethodSource("provideSingleCategoryPreferences")
    void whenUnauthenticatedSubscribeEmailPreferencesWithBadBody2Then400(Category category) {
        var dtoRequest = buildDto("demo@gmail.com", category.getId());

        dtoRequest.setEmail("nguyennt11032004"); // invalid email
        dtoRequest.setCategoryIds(List.of(category.getId()));
        webTestClient.post().uri("/email-preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(dtoRequest))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @ParameterizedTest
    @MethodSource("provideSingleCategoryPreferences")
    void whenUnauthenticatedSubscribeEmailPreferencesWithBadBody3Then400(Category category) {
        var dtoRequest = buildDto("nguyennt11032004@gmail.com", category.getId());
        dtoRequest.setFirstName("");
        dtoRequest.setCategoryIds(List.of(category.getId()));
        webTestClient.post().uri("/email-preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(dtoRequest))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @ParameterizedTest
    @MethodSource("provideSingleCategoryPreferences")
    void whenSubscribeEmailPreferencesWithEmailExistingThenUpdateAndReturn201(Category category) throws InterruptedException {
        var email = "demo@fpt.edu.vn";

        var dtoRequest = buildDto(email, category.getId());

        dtoRequest.setFirstName("change name");
        dtoRequest.setCategoryIds(null);
        webTestClient.post().uri("/email-preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(dtoRequest))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.email").isEqualTo(dtoRequest.getEmail())
                .jsonPath("$.firstName").isEqualTo("change name")
                .jsonPath("$.lastName").isEqualTo(dtoRequest.getLastName())
                .jsonPath("$.categories").isEmpty()
                .jsonPath("$.emailTopics").isNotEmpty()
                .jsonPath("$.id").isNotEmpty();
    }

    @ParameterizedTest
    @MethodSource("provideSingleEmailPreferences")
    void whenUnSubscribeEmailPreferencesWithEmailExistingThenNoContent(EmailPreferences emailPreferences) throws InterruptedException {
        var email = emailPreferences.getEmail();

        webTestClient.delete().uri("/email-preferences/" + email + "/unsubscribe")
                .exchange()
                .expectStatus().isNoContent();
    }

    private static EmailPreferencesRequestDto buildDto(String email, Long categoryId) {
        return new EmailPreferencesRequestDto(email, "Test first name",
                "Test last name", List.of(categoryId), List.of(EmailTopic.NEW_RELEASES, EmailTopic.WEEKLY_NEWSLETTER));
    }

}
