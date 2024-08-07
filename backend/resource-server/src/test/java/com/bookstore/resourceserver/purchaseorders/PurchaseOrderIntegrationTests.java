package com.bookstore.resourceserver.purchaseorders;

import com.bookstore.resourceserver.IntegrationTestsBase;
import com.bookstore.resourceserver.book.*;
import com.bookstore.resourceserver.purchaseorder.PaymentMethod;
import com.bookstore.resourceserver.purchaseorder.dto.LineItemRequest;
import com.bookstore.resourceserver.purchaseorder.dto.OrderRequest;
import com.bookstore.resourceserver.purchaseorder.dto.PaymentRequest;
import com.bookstore.resourceserver.purchaseorder.dto.UserInformation;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class PurchaseOrderIntegrationTests extends IntegrationTestsBase {

    static Book bookMock1;
    static Book bookMock2;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    BookService bookService;

//    @MockBean
//    private JavaMailSender javaMailSender;

    @BeforeAll
    static void setup() {
        bookMock1 = new Book();
        bookMock1.setIsbn("1234567891");
        bookMock1.setTitle("Title 1");
        bookMock1.setAuthor("Author 1");
        bookMock1.setPublisher("Publisher 1");
        bookMock1.setSupplier("Supplier 1");
        bookMock1.setPrice(210000L);
        bookMock1.setLanguage(Language.ENGLISH);
        bookMock1.setCoverType(CoverType.PAPERBACK);
        bookMock1.setNumberOfPages(25);
        bookMock1.setInventory(3);
        bookMock1.setMeasure(new Measure(120, 180, 10, 200));

        bookMock2 = new Book();
        bookMock2.setIsbn("1234567892");
        bookMock2.setTitle("Title 2");
        bookMock2.setAuthor("Author 2");
        bookMock2.setPublisher("Publisher 2");
        bookMock2.setSupplier("Supplier 2");
        bookMock2.setPrice(210000L);
        bookMock2.setLanguage(Language.ENGLISH);
        bookMock2.setCoverType(CoverType.PAPERBACK);
        bookMock2.setNumberOfPages(25);
        bookMock2.setInventory(3);
        bookMock2.setMeasure(new Measure(120, 180, 10, 200));
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
                .headers(headers -> headers.setBearerAuth(customerToken.getAccessToken()))
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
                .headers(headers -> headers.setBearerAuth(customerToken.getAccessToken()))
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


}
