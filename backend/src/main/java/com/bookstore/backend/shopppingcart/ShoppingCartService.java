package com.bookstore.backend.shopppingcart;

import com.bookstore.backend.shopppingcart.exception.ShoppingCartNotFoundException;
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
                .orElseThrow(() -> new ShoppingCartNotFoundException(cartId));
    }
}
