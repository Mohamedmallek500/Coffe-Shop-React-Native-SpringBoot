package com.bezkoder.spring.security.jwt.controllers;


import com.bezkoder.spring.security.jwt.models.CartItem;
import com.bezkoder.spring.security.jwt.service.CartService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
@PreAuthorize("hasAuthority('CLIENT')")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // =========================
    // GET CART
    // =========================
    @GetMapping
    public List<CartItem> getCart(Authentication authentication) {
        return cartService.getCart(authentication.getName());
    }

    // =========================
    // ADD PRODUCT
    // =========================
    @PostMapping("/add/{productId}")
    public CartItem addToCart(@PathVariable Long productId,
                              @RequestParam int quantity,
                              Authentication authentication) {

        return cartService.addToCart(
                authentication.getName(),
                productId,
                quantity
        );
    }

    // =========================
    // UPDATE QUANTITY
    // =========================
    @PutMapping("/{cartItemId}")
    public CartItem updateQuantity(@PathVariable Long cartItemId,
                                   @RequestParam int quantity) {
        return cartService.updateQuantity(cartItemId, quantity);
    }

    // =========================
    // REMOVE ITEM
    // =========================
    @DeleteMapping("/{cartItemId}")
    public void removeItem(@PathVariable Long cartItemId) {
        cartService.removeItem(cartItemId);
    }

    // =========================
    // CLEAR CART
    // =========================
    @DeleteMapping("/clear")
    public void clearCart(Authentication authentication) {
        cartService.clearCart(authentication.getName());
    }
}
