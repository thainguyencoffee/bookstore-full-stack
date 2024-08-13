package com.bookstore.resourceserver.purchaseorders;

import com.bookstore.resourceserver.IntegrationTestsBase;
import com.bookstore.resourceserver.book.*;
import com.bookstore.resourceserver.book.valuetype.Phone;
import com.bookstore.resourceserver.core.valuetype.AddressInformation;
import com.bookstore.resourceserver.purchaseorder.PaymentMethod;
import com.bookstore.resourceserver.purchaseorder.dto.LineItemRequest;
import com.bookstore.resourceserver.purchaseorder.dto.OrderRequest;
import com.bookstore.resourceserver.purchaseorder.dto.PaymentRequest;
import com.bookstore.resourceserver.core.valuetype.UserInformation;
import com.bookstore.resourceserver.purchaseorder.valuetype.BookType;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class PurchaseOrderIntegrationTests extends IntegrationTestsBase {

    @Autowired
    private WebTestClient webTestClient;

    @ParameterizedTest
    @MethodSource("provideDoubleBook")
    void postOrderThenCreated(Book bookOne, Book bookTwo) {
        OrderRequest orderRequest = buildOrderRequest(Map.of(bookOne, BookType.PRINT_BOOK, bookTwo, BookType.EBOOK));
        webTestClient.post()
                .uri("/orders")
                .bodyValue(orderRequest)
                .exchange()
                .expectStatus().isCreated();
    }

    @ParameterizedTest
    @MethodSource("provideDoubleBook")
    void whenAuthenticatedGetPaymentUrlThenSuccess(Book bookOne, Book bookTwo) {
        OrderRequest orderRequest = buildOrderRequest(Map.of(bookOne, BookType.PRINT_BOOK, bookTwo, BookType.EBOOK));
        String responseBody = webTestClient.post()
                .uri("/orders")
                .headers(headers -> headers.setBearerAuth(customerToken.getAccessToken()))
                .bodyValue(orderRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class)
                .returnResult().getResponseBody();
        String orderIdStr = JsonPath.parse(responseBody).read("$.id");
        UUID orderId = UUID.fromString(orderIdStr);

        webTestClient.post()
                .uri("/payment/vn-pay/payment-url")
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

    private static OrderRequest buildOrderRequest(Map<Book, BookType> bookMap) {
        var orderRequest = new OrderRequest();
        var lineItems = new ArrayList<LineItemRequest>();
        for (Map.Entry<Book, BookType> entry: bookMap.entrySet()) {
            LineItemRequest lineItemRequest = new LineItemRequest();
            lineItemRequest.setIsbn(entry.getKey().getIsbn());
            lineItemRequest.setQuantity(1);
            lineItemRequest.setBookType(entry.getValue());
            lineItems.add(lineItemRequest);
        }
        orderRequest.setLineItems(lineItems);
        var userInfo = new UserInformation();
        userInfo.setFirstName("Nguyen");
        userInfo.setLastName("Thai");
        userInfo.setEmail("nguyennt11032004@gmail.com");
        userInfo.setPhone(new Phone("987654321", 84));
        var addressInfo = new AddressInformation();
        addressInfo.setCity("Ha Noi");
        addressInfo.setZipCode("100000");
        addressInfo.setAddress("Ha Noi, Viet Nam");
        orderRequest.setUserInformation(userInfo);
        orderRequest.setAddressInformation(addressInfo);
        orderRequest.setPaymentMethod(PaymentMethod.VNPAY);
        return orderRequest;
    }

}
