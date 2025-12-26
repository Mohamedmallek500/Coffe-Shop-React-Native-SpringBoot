package com.bezkoder.spring.security.jwt.controllers;


import com.bezkoder.spring.security.jwt.models.Product;
import com.bezkoder.spring.security.jwt.service.ProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // PUBLIC
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllAvailable();
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productService.getById(id);
    }

    @GetMapping("/category/{categoryId}")
    public List<Product> getByCategory(@PathVariable Long categoryId) {
        return productService.getByCategory(categoryId);
    }

    // ADMIN
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/category/{categoryId}")
    public Product createProduct(@RequestBody Product product,
                                 @PathVariable Long categoryId) {
        return productService.create(product, categoryId);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id,
                                 @RequestBody Product product) {
        return productService.update(id, product);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.delete(id);
    }
}
