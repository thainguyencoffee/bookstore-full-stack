package com.bookstore.resourceserver.purchaseorder.dto;

import com.bookstore.resourceserver.core.valuetype.AddressInformation;
import com.bookstore.resourceserver.core.valuetype.UserInformation;
import com.bookstore.resourceserver.purchaseorder.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

@Getter
@Setter
public class OrderRequest {
    @Size(min = 1, message = "Order must have at least one line item")
    @Valid
    private List<LineItemRequest> lineItems;
    @Valid
    private UserInformation userInformation;
    private AddressInformation addressInformation;
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}

