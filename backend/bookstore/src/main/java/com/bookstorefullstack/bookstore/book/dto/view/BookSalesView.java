/*
 * @author thainguyencoffee
 */

/*
 * @author thainguyencoffee
 */

package com.bookstorefullstack.bookstore.book.dto.view;

public record BookSalesView(
        String isbn,
        String title,
        Integer totalPurchases
) {
}
