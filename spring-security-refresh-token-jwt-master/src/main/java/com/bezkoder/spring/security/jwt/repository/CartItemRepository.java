package com.bezkoder.spring.security.jwt.repository;


import com.bezkoder.spring.security.jwt.models.CartItem;
import com.bezkoder.spring.security.jwt.models.Product;
import com.bezkoder.spring.security.jwt.models.Utilisateur;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUser(Utilisateur user);

    Optional<CartItem> findByUserAndProduct(Utilisateur user, Product product);
    @Modifying
    @Transactional
    void deleteByUser(Utilisateur user);
}
