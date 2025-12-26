package com.bezkoder.spring.security.jwt.repository;

import com.bezkoder.spring.security.jwt.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
