package com.bookstore.resourceserver.shopppingcart;

import com.bookstore.resourceserver.core.exception.CustomNoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingCartService {

    private final ShoppingCartRepository repository;

    public Optional<ShoppingCart> findByCreatedBy(String username) {
        return repository.findByCreatedBy(username);
    }

    public ShoppingCart save(ShoppingCart shoppingCart) {
        return repository.save(shoppingCart);
    }

    public ShoppingCart findById(UUID cartId) {
        return repository.findById(cartId)
                .orElseThrow(() -> new CustomNoResultException(ShoppingCart.class, CustomNoResultException.Identifier.ID, cartId));
    }
}
