package com.bookstore.backend;

import com.bookstore.backend.book.*;
import com.bookstore.backend.orders.PaymentMethod;
import com.bookstore.backend.orders.dto.LineItemRequest;
import com.bookstore.backend.orders.dto.OrderRequest;
import com.bookstore.backend.orders.dto.PaymentRequest;
import com.bookstore.backend.orders.dto.UserInformation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class OrderServiceApplicationTests {

    private static KeycloakToken employeeToken;
    private static KeycloakToken customerToken;
    static Book bookMock1 = Book.builder()
            .isbn("1234567891")
            .title("Title 1")
            .author("Author 1")
            .publisher("Publisher 1")
            .supplier("Supplier 1")
            .price(210000L)
            .language(Language.ENGLISH)
            .coverType(CoverType.PAPERBACK)
            .numberOfPages(25)
            .inventory(3)
            .measure(new Measure(120, 180, 10, 200)).build();
    static Book bookMock2 = Book.builder()
            .isbn("1234567892")
            .title("Title 2")
            .author("Author 2")
            .publisher("Publisher 2")
            .supplier("Supplier 2")
            .price(210000L)
            .language(Language.ENGLISH)
            .coverType(CoverType.PAPERBACK)
            .inventory(3)
            .numberOfPages(25)
            .measure(new Measure(120, 180, 10, 200)).build();

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    BookService bookService;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:16"));

    @Container
    static KeycloakContainer keycloak = new KeycloakContainer(
            "quay.io/keycloak/keycloak:23.0")
            .withRealmImportFile("bookstore-realm.json");

    @BeforeAll
    static void setup() {
        WebClient webClient = WebClient.builder()
                .baseUrl(keycloak.getAuthServerUrl() + "realms/bookstore/protocol/openid-connect/token")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();

        employeeToken = authenticatedWith("employee", "1", webClient);
        customerToken = authenticatedWith("user", "1", webClient);
    }

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> keycloak.getAuthServerUrl() + "realms/bookstore");
    }

    @Test
    void postOrderThenCreated() {
        Map.Entry<String, Integer> book1 = Map.entry("1234567891", 1);
        Map.Entry<String, Integer> book2 = Map.entry("1234567892", 2);
        OrderRequest orderRequest = buildOrderRequest(Map.of(book1.getKey(), book1.getValue(), book2.getKey(), book2.getValue()));
        // Mock
        when(bookService.findByIsbn(book1.getKey())).thenReturn(bookMock1);
        when(bookService.findByIsbn(book2.getKey())).thenReturn(bookMock2);
        webTestClient.post()
                .uri("/api/orders")
                .bodyValue(orderRequest)
                .exchange()
                .expectStatus().isCreated();
    }


    @Test
    void whenAuthenticatedGetPaymentUrlThenSuccess() {
        Map.Entry<String, Integer> book1 = Map.entry("1234567891", 1);
        Map.Entry<String, Integer> book2 = Map.entry("1234567892", 2);
        OrderRequest orderRequest = buildOrderRequest(Map.of(book1.getKey(), book1.getValue(), book2.getKey(), book2.getValue()));
        // Mock
        when(bookService.findByIsbn(book1.getKey())).thenReturn(bookMock1);
        when(bookService.findByIsbn(book2.getKey())).thenReturn(bookMock2);
        String responseBody = webTestClient.post()
                .uri("/api/orders")
                .headers(headers -> headers.setBearerAuth(customerToken.accessToken))
                .bodyValue(orderRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class)
                .returnResult().getResponseBody();
        String orderIdStr = JsonPath.parse(responseBody).read("$.id");
        UUID orderId = UUID.fromString(orderIdStr);

        webTestClient.post()
                .uri("/api/payment/vn-pay/payment-url")
                .body(BodyInserters.fromValue(new PaymentRequest(orderId)))
                .headers(headers -> headers.setBearerAuth(customerToken.accessToken))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(jsonValue -> {
                    DocumentContext documentContext = JsonPath.parse(jsonValue);
                    assertThat(documentContext.read("$.paymentUrl", String.class)).isNotBlank();
                });
    }

    private static OrderRequest buildOrderRequest(Map<String, Integer> map) {
        var orderRequest = new OrderRequest();
        var lineItems = new ArrayList<LineItemRequest>();
        for (Map.Entry<String, Integer> entry: map.entrySet()) {
            LineItemRequest lineItemRequest = new LineItemRequest();
            lineItemRequest.setIsbn(entry.getKey());
            lineItemRequest.setQuantity(entry.getValue());
            lineItems.add(lineItemRequest);
        }
        orderRequest.setLineItems(lineItems);
        var userInfo = new UserInformation();
        userInfo.setFullName("Nguyen Thai Nguyen");
        userInfo.setEmail("nguyennt11032004@gmail.com");
        userInfo.setPhoneNumber("0987654321");
        userInfo.setCity("Ha Noi");
        userInfo.setZipCode("100000");
        userInfo.setAddress("Ha Noi, Viet Nam");
        orderRequest.setUserInformation(userInfo);
        orderRequest.setPaymentMethod(PaymentMethod.VNPAY);
        return orderRequest;
    }


    private static KeycloakToken authenticatedWith(String username, String password, WebClient webClient) {
        return webClient
                .post()
                .body(BodyInserters.fromFormData("grant_type", "password")
                        .with("client_id", "edge-service")
                        .with("client_secret", "cT5pq7W3XStcuFVQMhjPbRj57Iqxcu4n")
                        .with("username", username)
                        .with("password", password))
                .retrieve()
                .bodyToMono(KeycloakToken.class)
                .block();
    }

    private record KeycloakToken(String accessToken) {
        @JsonCreator
        private KeycloakToken(@JsonProperty("access_token") String accessToken) {
            this.accessToken = accessToken;
        }
    }

}
