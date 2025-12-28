package com.bezkoder.spring.security.jwt.controllers;


import com.bezkoder.spring.security.jwt.models.CartItem;
import com.bezkoder.spring.security.jwt.service.CartService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
                              @RequestParam(required = false) List<Long> optionIds,
                              Authentication authentication) {

        return cartService.addToCart(
                authentication.getName(),
                productId,
                quantity,
                optionIds
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

    // Mettre à jour les options d'un item
    @PutMapping("/{cartItemId}/options")
    public CartItem updateOptions(@PathVariable Long cartItemId,
                                  @RequestParam List<Long> optionIds) {
        return cartService.updateOptions(cartItemId, optionIds);
    }

    // Obtenir le total du panier
    @GetMapping("/total")
    public BigDecimal getCartTotal(Authentication authentication) {
        return cartService.getCartTotal(authentication.getName());
    }
}
