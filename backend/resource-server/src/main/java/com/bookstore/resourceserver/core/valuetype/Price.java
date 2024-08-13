package com.bookstore.resourceserver.core.valuetype;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Price {
    private Long originalPrice;
    private Long discountedPrice = 0L;
    private String currencyPrice;
}
