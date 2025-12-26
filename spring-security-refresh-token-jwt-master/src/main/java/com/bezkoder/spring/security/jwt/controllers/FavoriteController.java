package com.bezkoder.spring.security.jwt.controllers;

import com.bezkoder.spring.security.jwt.models.Favorite;
import com.bezkoder.spring.security.jwt.service.FavoriteService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*")
@PreAuthorize("hasAuthority('CLIENT')")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    // =========================
    // GET FAVORITES
    // =========================
    @GetMapping
    public List<Favorite> getFavorites(Authentication authentication) {
        return favoriteService.getFavorites(authentication.getName());
    }

    // =========================
    // ADD FAVORITE
    // =========================
    @PostMapping("/{productId}")
    public Favorite addFavorite(@PathVariable Long productId,
                                Authentication authentication) {
        return favoriteService.addFavorite(authentication.getName(), productId);
    }

    // =========================
    // REMOVE FAVORITE
    // =========================
    @DeleteMapping("/{productId}")
    public void removeFavorite(@PathVariable Long productId,
                               Authentication authentication) {
        favoriteService.removeFavorite(authentication.getName(), productId);
    }
}
