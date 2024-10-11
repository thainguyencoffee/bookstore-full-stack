package com.bookstorefullstack.bookstore.shopppingcart;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartItemService {

    private final CartItemRepository repository;

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public CartItem save(CartItem item) {
        return repository.save(item);
    }
}
