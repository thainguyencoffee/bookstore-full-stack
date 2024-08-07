package com.bookstore.resourceserver.shopppingcart;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShoppingCartRepository extends CrudRepository<ShoppingCart, UUID> {

    Optional<ShoppingCart> findByCreatedBy(String createdBy);

}
