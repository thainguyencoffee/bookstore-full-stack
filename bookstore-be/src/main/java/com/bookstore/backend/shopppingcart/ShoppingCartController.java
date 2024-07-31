package com.bookstore.backend.shopppingcart;

import com.bookstore.backend.book.BookService;
import com.bookstore.backend.core.exception.CustomNoResultException;
import com.bookstore.backend.shopppingcart.dto.DeleteAllCartRequest;
import com.bookstore.backend.core.exception.shoppingcart.ShoppingCartAlreadyExistingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ShoppingCartController {

    private final CartItemService cartItemService;
    private final BookService bookService;
    private final ShoppingCartService shoppingCartService;

    @GetMapping("/my-cart")
    public ResponseEntity<ShoppingCart> getMyCart(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaim(StandardClaimNames.PREFERRED_USERNAME);
        log.info("{} attempt get shopping cart", username);
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
    public ResponseEntity<ShoppingCart> addToCart(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID cartId, @RequestBody CartItem cartItem) {
        String username = jwt.getClaim(StandardClaimNames.PREFERRED_USERNAME);
        log.info("{} attempt get cart id {}", username, cartId);
        ShoppingCart shoppingCart = shoppingCartService.findById(cartId);
        // find book by isbn
        bookService.findByIsbn(cartItem.getIsbn());
        for (CartItem item : shoppingCart.getCartItems()) {
            if (item.getIsbn().equals(cartItem.getIsbn())) {
                int quantityChanged = item.getQuantity() + cartItem.getQuantity();
                if (quantityChanged <= 0) {
                    cartItemService.deleteById(item.getId());
                    shoppingCart.getCartItems().remove(item);
                    return ResponseEntity.ok(shoppingCart);
                } else {
                    item.setQuantity(quantityChanged);
                    cartItemService.save(item);
                    return ResponseEntity.ok(shoppingCart);
                }
            }
        }
        cartItem.setCartId(shoppingCart.getId());
        shoppingCart.getCartItems().add(cartItem);
        return ResponseEntity.ok(shoppingCartService.save(shoppingCart));
    }

    @DeleteMapping("/{cartId}/delete-cart-item")
    public ResponseEntity<ShoppingCart> deleteCartItem(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID cartId, @RequestParam("isbn") String isbn) {
        String username = jwt.getClaim(StandardClaimNames.PREFERRED_USERNAME);
        log.info("{} attempt delete cart item with cart id {} and isbn {}", username, cartId, isbn);
        ShoppingCart shoppingCart = shoppingCartService.findById(cartId);
        // find book by isbn
        bookService.findByIsbn(isbn);
        for (CartItem item : shoppingCart.getCartItems()) {
            if (item.getIsbn().equals(isbn)) {
                shoppingCart.getCartItems().remove(item);
                return ResponseEntity.ok(shoppingCartService.save(shoppingCart));
            }
        }
        throw new CustomNoResultException(CartItem.class, CustomNoResultException.Identifier.ISBN, isbn);
    }

    @PostMapping("/{cartId}/delete-all-cart-item")
    public ResponseEntity<ShoppingCart> deleteCartItem(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID cartId, @RequestBody DeleteAllCartRequest reqBody) {
        String username = jwt.getClaim(StandardClaimNames.PREFERRED_USERNAME);
        log.info("{} attempt delete all cart item with cart id {}.", username, cartId);
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
