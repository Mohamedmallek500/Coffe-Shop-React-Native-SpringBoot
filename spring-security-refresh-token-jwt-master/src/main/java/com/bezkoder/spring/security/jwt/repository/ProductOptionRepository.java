package com.bezkoder.spring.security.jwt.repository;

import com.bezkoder.spring.security.jwt.models.Product;
import com.bezkoder.spring.security.jwt.models.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    List<ProductOption> findByProduct(Product product);
}