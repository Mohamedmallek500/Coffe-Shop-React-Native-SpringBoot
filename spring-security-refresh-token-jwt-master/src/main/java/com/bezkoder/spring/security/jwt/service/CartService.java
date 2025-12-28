package com.bezkoder.spring.security.jwt.service;

import com.bezkoder.spring.security.jwt.models.CartItem;
import com.bezkoder.spring.security.jwt.models.Product;
import com.bezkoder.spring.security.jwt.models.ProductOption;
import com.bezkoder.spring.security.jwt.models.Utilisateur;
import com.bezkoder.spring.security.jwt.repository.CartItemRepository;
import com.bezkoder.spring.security.jwt.repository.ProductOptionRepository;
import com.bezkoder.spring.security.jwt.repository.ProductRepository;
import com.bezkoder.spring.security.jwt.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final UtilisateurRepository utilisateurRepository;

    public CartService(CartItemRepository cartItemRepository,
                       ProductRepository productRepository,
                       ProductOptionRepository productOptionRepository,
                       UtilisateurRepository utilisateurRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.productOptionRepository = productOptionRepository;
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
    // ADD PRODUCT (avec options)
    // =========================
    @Transactional
    public CartItem addToCart(String email, Long productId, int quantity, List<Long> optionIds) {

        Utilisateur user = getCurrentUser(email);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Vérifier que le produit est disponible
        if (!product.isAvailable()) {
            throw new RuntimeException("Product is not available");
        }

        // Calculer le prix final avec les options
        BigDecimal finalPrice = product.getBasePrice();

        List<ProductOption> selectedOptions = new ArrayList<>();

        if (optionIds != null && !optionIds.isEmpty()) {
            for (Long optionId : optionIds) {
                ProductOption option = productOptionRepository.findById(optionId)
                        .orElseThrow(() -> new RuntimeException("Option not found: " + optionId));

                // Vérifier que l'option appartient bien à ce produit
                if (!option.getProduct().getId().equals(productId)) {
                    throw new RuntimeException("Option " + optionId + " does not belong to product " + productId);
                }

                selectedOptions.add(option);

                // Ajouter le modificateur de prix (peut être négatif pour une réduction)
                if (option.getPriceModifier() != null) {
                    finalPrice = finalPrice.add(option.getPriceModifier());
                }
            }
        }

        // Vérifier si le produit avec les MÊMES options existe déjà dans le panier
        // Note: Pour simplifier, on crée toujours un nouveau CartItem
        // Si vous voulez fusionner les items identiques, il faut comparer les options

        CartItem cartItem = new CartItem();
        cartItem.setUser(user);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItem.setUnitPrice(finalPrice);
        cartItem.setSelectedOptions(selectedOptions);

        return cartItemRepository.save(cartItem);
    }

    // =========================
    // ADD PRODUCT (sans options - pour compatibilité)
    // =========================
    @Transactional
    public CartItem addToCart(String email, Long productId, int quantity) {
        return addToCart(email, productId, quantity, null);
    }

    // =========================
    // UPDATE QUANTITY
    // =========================
    @Transactional
    public CartItem updateQuantity(Long cartItemId, int quantity) {

        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }

    // =========================
    // UPDATE OPTIONS (nouvelle méthode)
    // =========================
    @Transactional
    public CartItem updateOptions(Long cartItemId, List<Long> optionIds) {

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        Product product = item.getProduct();

        // Recalculer le prix avec les nouvelles options
        BigDecimal finalPrice = product.getBasePrice();

        List<ProductOption> selectedOptions = new ArrayList<>();

        if (optionIds != null && !optionIds.isEmpty()) {
            for (Long optionId : optionIds) {
                ProductOption option = productOptionRepository.findById(optionId)
                        .orElseThrow(() -> new RuntimeException("Option not found: " + optionId));

                // Vérifier que l'option appartient bien à ce produit
                if (!option.getProduct().getId().equals(product.getId())) {
                    throw new RuntimeException("Option does not belong to this product");
                }

                selectedOptions.add(option);

                if (option.getPriceModifier() != null) {
                    finalPrice = finalPrice.add(option.getPriceModifier());
                }
            }
        }

        item.setSelectedOptions(selectedOptions);
        item.setUnitPrice(finalPrice);

        return cartItemRepository.save(item);
    }

    // =========================
    // REMOVE ITEM
    // =========================
    @Transactional
    public void removeItem(Long cartItemId) {
        if (!cartItemRepository.existsById(cartItemId)) {
            throw new RuntimeException("Cart item not found");
        }
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

    // =========================
    // GET CART TOTAL (utilitaire)
    // =========================
    public BigDecimal getCartTotal(String email) {
        List<CartItem> items = getCart(email);

        return items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // =========================
    // GET CART ITEM COUNT (utilitaire)
    // =========================
    public int getCartItemCount(String email) {
        List<CartItem> items = getCart(email);

        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}