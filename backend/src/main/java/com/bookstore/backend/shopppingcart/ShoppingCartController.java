package com.bookstore.backend.shopppingcart;

import com.bookstore.backend.book.Book;
import com.bookstore.backend.book.BookService;
import com.bookstore.backend.book.exception.BookNotEnoughInventoryException;
import com.bookstore.backend.shopppingcart.dto.DeleteAllCartRequest;
import com.bookstore.backend.shopppingcart.exception.CartItemNotFoundException;
import com.bookstore.backend.shopppingcart.exception.ShoppingCartAlreadyExistingException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "api/shopping-carts", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ShoppingCartController {

    private final CartItemService cartItemService;
    private final BookService bookService;
    private final ShoppingCartService shoppingCartService;

    @GetMapping("/my-cart")
    public ResponseEntity<ShoppingCart> getMyCart(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaim(StandardClaimNames.PREFERRED_USERNAME);
        try {
            Optional<ShoppingCart> byCreatedBy = shoppingCartService.findByCreatedBy(username);
            if (byCreatedBy.isPresent()) {
                return ResponseEntity.ok(byCreatedBy.get());
            } else {
                ShoppingCart shoppingCart = shoppingCartService.save(new ShoppingCart());
                return ResponseEntity.created(URI.create(shoppingCart.getId().toString())).body(shoppingCart);
            }
        } catch (DataIntegrityViolationException ex) {
            throw new ShoppingCartAlreadyExistingException(username);
        }
    }

    @PostMapping("/{cartId}/add-to-cart")
    public ResponseEntity<ShoppingCart> addToCart(@PathVariable UUID cartId, @RequestBody CartItem cartItem) {
        ShoppingCart shoppingCart = shoppingCartService.findById(cartId);
        // find book by isbn
        Book book = bookService.findByIsbn(cartItem.getIsbn());
        for (CartItem item : shoppingCart.getCartItems()) {
            if (item.getIsbn().equals(cartItem.getIsbn())) {
                int quantityChanged = item.getQuantity() + cartItem.getQuantity();
                if (quantityChanged <= 0) {
                    cartItemService.deleteById(item.getId());
                    shoppingCart.getCartItems().remove(item);
                    return ResponseEntity.ok(shoppingCart);
                } else if (quantityChanged > book.getInventory()) {
                    throw new BookNotEnoughInventoryException(cartItem.getIsbn());
                } else {
                    item.setQuantity(quantityChanged);
                    cartItemService.save(item);
                    return ResponseEntity.ok(shoppingCart);
                }
            }
        }
        if (cartItem.getQuantity() > book.getInventory()) {
            throw new BookNotEnoughInventoryException(cartItem.getIsbn());
        }
        cartItem.setCartId(shoppingCart.getId());
        shoppingCart.getCartItems().add(cartItem);
        return ResponseEntity.ok(shoppingCartService.save(shoppingCart));
    }

    @DeleteMapping("/{cartId}/delete-cart-item")
    public ResponseEntity<ShoppingCart> deleteCartItem(@PathVariable UUID cartId, @RequestParam("isbn") String isbn) {
        ShoppingCart shoppingCart = shoppingCartService.findById(cartId);
        // find book by isbn
        bookService.findByIsbn(isbn);
        for (CartItem item : shoppingCart.getCartItems()) {
            if (item.getIsbn().equals(isbn)) {
                shoppingCart.getCartItems().remove(item);
                return ResponseEntity.ok(shoppingCartService.save(shoppingCart));
            }
        }
        throw new CartItemNotFoundException(isbn);
    }

    @PostMapping("/{cartId}/delete-all-cart-item")
    public ResponseEntity<ShoppingCart> deleteCartItem(@PathVariable UUID cartId, @RequestBody DeleteAllCartRequest reqBody) {
        ShoppingCart shoppingCart = shoppingCartService.findById(cartId);
        // find book by isbn
        reqBody.getIsbn().forEach(itemShouldDelete -> {
            bookService.findByIsbn(itemShouldDelete);
            Iterator<CartItem> iterator = shoppingCart.getCartItems().iterator();
            while (iterator.hasNext()) {
                CartItem item = iterator.next();
                if (item.getIsbn().equals(itemShouldDelete)) {
                    iterator.remove(); // Sử dụng iterator để xóa một cách an toàn
                }
            }
        });
        return ResponseEntity.ok(shoppingCartService.save(shoppingCart));
    }

}
