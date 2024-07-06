package com.bookstore.backend.shopppingcart;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
interface ShoppingCartRepository extends CrudRepository<ShoppingCart, UUID> {

    Optional<ShoppingCart> findByCreatedBy(String createdBy);

}
