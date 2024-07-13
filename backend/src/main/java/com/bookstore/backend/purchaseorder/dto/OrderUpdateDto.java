package com.bookstore.backend.purchaseorder.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;

@Getter
@Setter
public class OrderUpdateDto {
    @Valid
    private UserInformation userInformation;
    private String createdBy;
    private String lastModifiedBy;
}
