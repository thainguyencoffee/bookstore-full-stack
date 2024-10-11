package com.bookstorefullstack.bookstore.purchaseorder.dto;

import com.bookstorefullstack.bookstore.core.valuetype.UserInformation;
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
