package com.bezkoder.spring.security.jwt.repository;

import com.bezkoder.spring.security.jwt.models.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

    Optional<Discount> findByCodeAndIsActiveTrue(String code);
}
