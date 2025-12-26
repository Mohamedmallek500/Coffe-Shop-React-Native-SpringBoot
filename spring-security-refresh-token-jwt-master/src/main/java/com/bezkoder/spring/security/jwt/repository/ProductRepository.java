package com.bezkoder.spring.security.jwt.repository;

import com.bezkoder.spring.security.jwt.models.Product;
import com.bezkoder.spring.security.jwt.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByIsAvailableTrue();

    List<Product> findByCategory(Category category);
}
