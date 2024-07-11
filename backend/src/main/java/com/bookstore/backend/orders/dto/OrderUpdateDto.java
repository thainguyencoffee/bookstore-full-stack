package com.bookstore.backend.orders.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;

@Getter
@Setter
public class OrderUpdateDto {
    @Valid
    private UserInformation userInformation;
}
