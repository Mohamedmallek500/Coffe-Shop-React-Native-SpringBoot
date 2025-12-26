package com.bezkoder.spring.security.jwt.service;


import com.bezkoder.spring.security.jwt.models.Category;
import com.bezkoder.spring.security.jwt.models.Product;
import com.bezkoder.spring.security.jwt.repository.CategoryRepository;
import com.bezkoder.spring.security.jwt.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // PUBLIC
    public List<Product> getAllAvailable() {
        return productRepository.findByIsAvailableTrue();
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public List<Product> getByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return productRepository.findByCategory(category);
    }

    // ADMIN
    public Product create(Product product, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        product.setCategory(category);
        return productRepository.save(product);
    }

    public Product update(Long id, Product updatedProduct) {
        Product product = getById(id);

        product.setName(updatedProduct.getName());
        product.setDescription(updatedProduct.getDescription());
        product.setBasePrice(updatedProduct.getBasePrice());
        product.setImageUrl(updatedProduct.getImageUrl());
        product.setAvailable(updatedProduct.isAvailable());

        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}
