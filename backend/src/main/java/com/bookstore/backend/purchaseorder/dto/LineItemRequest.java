package com.bookstore.backend.purchaseorder.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class LineItemRequest {

    @NotBlank(message = "ISBN is required")
    private String isbn;
    @Min(value = 1, message = "Quantity must be greater than 0")
    private Integer quantity;

}
