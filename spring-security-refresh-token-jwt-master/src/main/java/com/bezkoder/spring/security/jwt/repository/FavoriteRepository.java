package com.bezkoder.spring.security.jwt.repository;

import com.bezkoder.spring.security.jwt.models.Favorite;
import com.bezkoder.spring.security.jwt.models.Product;
import com.bezkoder.spring.security.jwt.models.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUser(Utilisateur user);

    Optional<Favorite> findByUserAndProduct(Utilisateur user, Product product);

    void deleteByUserAndProduct(Utilisateur user, Product product);
}
