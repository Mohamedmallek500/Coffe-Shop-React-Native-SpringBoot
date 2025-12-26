package com.bezkoder.spring.security.jwt.service;

import com.bezkoder.spring.security.jwt.models.Favorite;
import com.bezkoder.spring.security.jwt.models.Product;
import com.bezkoder.spring.security.jwt.models.Utilisateur;
import com.bezkoder.spring.security.jwt.repository.FavoriteRepository;
import com.bezkoder.spring.security.jwt.repository.ProductRepository;
import com.bezkoder.spring.security.jwt.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;
    private final UtilisateurRepository utilisateurRepository;

    public FavoriteService(FavoriteRepository favoriteRepository,
                           ProductRepository productRepository,
                           UtilisateurRepository utilisateurRepository) {
        this.favoriteRepository = favoriteRepository;
        this.productRepository = productRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    private Utilisateur getCurrentUser(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // =========================
    // GET FAVORITES
    // =========================
    public List<Favorite> getFavorites(String email) {
        Utilisateur user = getCurrentUser(email);
        return favoriteRepository.findByUser(user);
    }

    // =========================
    // ADD FAVORITE
    // =========================
    @Transactional
    public Favorite addFavorite(String email, Long productId) {

        Utilisateur user = getCurrentUser(email);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        favoriteRepository.findByUserAndProduct(user, product)
                .ifPresent(f -> {
                    throw new RuntimeException("Product already in favorites");
                });

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);

        return favoriteRepository.save(favorite);
    }

    // =========================
    // REMOVE FAVORITE
    // =========================
    @Transactional
    public void removeFavorite(String email, Long productId) {

        Utilisateur user = getCurrentUser(email);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        favoriteRepository.deleteByUserAndProduct(user, product);
    }
}
