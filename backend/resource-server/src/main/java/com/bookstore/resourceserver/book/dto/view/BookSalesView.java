/*
 * @author thainguyencoffee
 */

/*
 * @author thainguyencoffee
 */

package com.bookstore.resourceserver.book.dto.view;

public record BookSalesView(
        String isbn,
        String title,
        Integer totalPurchases
) {
}
