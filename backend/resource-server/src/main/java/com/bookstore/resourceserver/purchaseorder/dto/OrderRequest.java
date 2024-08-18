package com.bookstore.resourceserver.purchaseorder.dto;

import com.bookstore.resourceserver.core.valuetype.AddressInformation;
import com.bookstore.resourceserver.core.valuetype.UserInformation;
import com.bookstore.resourceserver.purchaseorder.PaymentMethod;
import lombok.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

@Getter
@Setter
public class OrderRequest {
    @Size(min = 1, message = "PurchaseOrder must have at least one line item")
    @Valid
    private List<LineItemRequest> lineItems;
    @Valid
    private UserInformation userInformation;
    private AddressInformation addressInformation;
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @Data
    public static class LineItemRequest {
        @NotEmpty(message = "Book ISBN of line item must be not empty.")
        @Pattern(regexp = "^([0-9]{10}|[0-9]{13})$", message = "The ISBN of book must contain 13 digits.")
        private String isbn;
        @Pattern(regexp = "(?i)^(EBOOK|PRINT_BOOK)$")
        private String bookType;
        @Min(value = 1, message = "Quantity must be greater than 0")
        private Integer quantity;
    }


}

