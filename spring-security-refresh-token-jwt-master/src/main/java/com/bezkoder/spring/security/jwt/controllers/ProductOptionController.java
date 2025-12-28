package com.bezkoder.spring.security.jwt.controllers;

import com.bezkoder.spring.security.jwt.models.ProductOption;
import com.bezkoder.spring.security.jwt.service.ProductOptionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-options")
@CrossOrigin(origins = "*")
public class ProductOptionController {

    private final ProductOptionService productOptionService;

    public ProductOptionController(ProductOptionService productOptionService) {
        this.productOptionService = productOptionService;
    }

    // =========================
    // GET OPTIONS FOR PRODUCT (PUBLIC)
    // =========================
    @GetMapping("/product/{productId}")
    public List<ProductOption> getOptionsByProduct(@PathVariable Long productId) {
        return productOptionService.getByProduct(productId);
    }

    // =========================
    // GET SINGLE OPTION (PUBLIC)
    // =========================
    @GetMapping("/{id}")
    public ProductOption getOption(@PathVariable Long id) {
        return productOptionService.getById(id);
    }

    // =========================
    // CREATE OPTION (ADMIN)
    // =========================
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/product/{productId}")
    public ProductOption createOption(@PathVariable Long productId,
                                      @RequestBody ProductOption option) {
        return productOptionService.create(productId, option);
    }

    // =========================
    // UPDATE OPTION (ADMIN)
    // =========================
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ProductOption updateOption(@PathVariable Long id,
                                      @RequestBody ProductOption option) {
        return productOptionService.update(id, option);
    }

    // =========================
    // DELETE OPTION (ADMIN)
    // =========================
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteOption(@PathVariable Long id) {
        productOptionService.delete(id);
    }
}