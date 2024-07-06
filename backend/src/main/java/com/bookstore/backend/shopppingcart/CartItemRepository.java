package com.bookstore.backend.shopppingcart;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface CartItemRepository extends CrudRepository<CartItem, Long> {

}
