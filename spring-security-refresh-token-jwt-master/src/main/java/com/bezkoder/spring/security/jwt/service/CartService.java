package com.bezkoder.spring.security.jwt.service;


import com.bezkoder.spring.security.jwt.models.CartItem;
import com.bezkoder.spring.security.jwt.models.Product;
import com.bezkoder.spring.security.jwt.models.Utilisateur;
import com.bezkoder.spring.security.jwt.repository.CartItemRepository;
import com.bezkoder.spring.security.jwt.repository.ProductRepository;
import com.bezkoder.spring.security.jwt.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UtilisateurRepository utilisateurRepository;

    public CartService(CartItemRepository cartItemRepository,
                       ProductRepository productRepository,
                       UtilisateurRepository utilisateurRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    private Utilisateur getCurrentUser(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // =========================
    // GET CART
    // =========================
    public List<CartItem> getCart(String email) {
        Utilisateur user = getCurrentUser(email);
        return cartItemRepository.findByUser(user);
    }

    // =========================
    // ADD PRODUCT
    // =========================
    @Transactional
    public CartItem addToCart(String email, Long productId, int quantity) {

        Utilisateur user = getCurrentUser(email);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem cartItem = cartItemRepository
                .findByUserAndProduct(user, product)
                .orElse(null);

        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setUnitPrice(product.getBasePrice());
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }

        return cartItemRepository.save(cartItem);
    }

    // =========================
    // UPDATE QUANTITY
    // =========================
    public CartItem updateQuantity(Long cartItemId, int quantity) {

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }

    // =========================
    // REMOVE ITEM
    // =========================
    public void removeItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    // =========================
    // CLEAR CART
    // =========================
    @Transactional
    public void clearCart(String email) {
        Utilisateur user = getCurrentUser(email);
        cartItemRepository.deleteByUser(user);
    }
}
