package com.bookstore.resourceserver.shopppingcart;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface CartItemRepository extends CrudRepository<CartItem, Long> {
    void deleteByIsbn(String isbn);
}
