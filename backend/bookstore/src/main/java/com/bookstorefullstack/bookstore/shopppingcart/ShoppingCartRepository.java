package com.bookstorefullstack.bookstore.shopppingcart;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShoppingCartRepository extends ListCrudRepository<ShoppingCart, UUID> {

    Optional<ShoppingCart> findByCreatedBy(String createdBy);

}
