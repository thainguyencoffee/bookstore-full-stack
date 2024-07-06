package com.bookstore.backend.orders.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class LineItemRequest {

    @NotBlank(message = "ISBN is required")
    private String isbn;
    @Min(value = 1, message = "Quantity must be greater than 0")
    private Integer quantity;

}
