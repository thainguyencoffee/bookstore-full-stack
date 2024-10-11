package com.bookstorefullstack.bookstore.book.valuetype;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Phone {

    private String phone;
    private Integer countryCode;

    public Phone(String phone, Integer countryCode) {
        this.phone = phone;
        this.countryCode = countryCode;
    }

}
