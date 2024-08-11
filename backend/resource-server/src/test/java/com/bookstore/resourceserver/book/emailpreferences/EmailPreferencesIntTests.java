package com.bookstore.resourceserver.book.emailpreferences;

import com.bookstore.resourceserver.IntegrationTestsBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.List;

public class EmailPreferencesIntTests extends IntegrationTestsBase {

    @Autowired
    private WebTestClient webTestClient;

//    @BeforeEach
//    void setUp() {
//        webTestClient = webTestClient.mutate()
//                .responseTimeout(Duration.ofMillis(30000))
//                .build();
//    }

    @Test
    void whenUnauthenticatedSubscribeEmailPreferencesWithValidDataThen201() {
        var dtoRequest = buildDto("demo@gmail.com");

        webTestClient.post().uri("/api/email-preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(dtoRequest))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.email").isEqualTo(dtoRequest.getEmail())
                .jsonPath("$.id").isNotEmpty();
    }

    @Test
    void whenUnauthenticatedSubscribeEmailPreferencesWithCategoryNotfoundThen404() {
        var dtoRequest = buildDto("demo@gmail.com");

        dtoRequest.setCategoryIds(List.of(10000L, 9999999L));
        webTestClient.post().uri("/api/email-preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(dtoRequest))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenUnauthenticatedSubscribeEmailPreferencesWithBadBodyThen400() {
        var dtoRequest = buildDto("demo@gmail.com");

        dtoRequest.setEmail("");
        dtoRequest.setCategoryIds(List.of(10000L));
        webTestClient.post().uri("/api/email-preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(dtoRequest))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void whenUnauthenticatedSubscribeEmailPreferencesWithBadBody2Then400() {
        var dtoRequest = buildDto("demo@gmail.com");

        dtoRequest.setEmail("nguyennt11032004"); // invalid email
        dtoRequest.setCategoryIds(List.of(10000L));
        webTestClient.post().uri("/api/email-preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(dtoRequest))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void whenUnauthenticatedSubscribeEmailPreferencesWithBadBody3Then400() {
        var dtoRequest = buildDto("nguyennt11032004@gmail.com");
        dtoRequest.setFirstName("");
        dtoRequest.setCategoryIds(List.of(10000L));
        webTestClient.post().uri("/api/email-preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(dtoRequest))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void whenSubscribeEmailPreferencesWithEmailExistingThenUpdateAndReturn201() throws InterruptedException {
        var email = "demo@fpt.edu.vn";

        var dtoRequest = buildDto(email);

        dtoRequest.setFirstName("change name");
        dtoRequest.setCategoryIds(null);
        webTestClient.post().uri("/api/email-preferences")
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

    @Test
    void whenUnSubscribeEmailPreferencesWithEmailExistingThenNoContent() throws InterruptedException {
        var email = "nguyennt11032004@gmail.vn";

        webTestClient.delete().uri("/api/email-preferences/" + email + "/unsubscribe")
                .exchange()
                .expectStatus().isNoContent();
    }

    private static EmailPreferencesRequestDto buildDto(String email) {
        return new EmailPreferencesRequestDto(email, "Test first name",
                "Test last name", List.of(10000L, 10001L), List.of(EmailTopic.NEW_RELEASES, EmailTopic.WEEKLY_NEWSLETTER));
    }

}
