package com.bezkoder.spring.security.jwt.repository;

import com.bezkoder.spring.security.jwt.models.Order;
import com.bezkoder.spring.security.jwt.models.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(Utilisateur user);
}
