package com.bookstore.resourceserver.purchaseorder.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.Valid;

@Getter
@Setter
public class OrderUpdateDto {
    @Valid
    private UserInformation userInformation;
    private String createdBy;
    private String lastModifiedBy;
}
