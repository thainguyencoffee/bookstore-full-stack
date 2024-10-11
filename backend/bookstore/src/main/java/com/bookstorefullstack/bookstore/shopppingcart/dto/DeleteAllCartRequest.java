package com.bookstorefullstack.bookstore.shopppingcart.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DeleteAllCartRequest {
    private List<String> isbn;
}
