package com.bookstorefullstack.bookstore.core.valuetype;

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

    public void addOriginalPrice(Long originalPrice) {
        this.originalPrice += originalPrice;
    }

    public void addDiscountedPrice(Long discountedPrice) {
        this.discountedPrice += discountedPrice;
    }
}
