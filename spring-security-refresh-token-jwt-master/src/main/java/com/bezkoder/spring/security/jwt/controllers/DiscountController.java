package com.bezkoder.spring.security.jwt.controllers;

import com.bezkoder.spring.security.jwt.models.Discount;
import com.bezkoder.spring.security.jwt.service.DiscountService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discounts")
@CrossOrigin(origins = "*")
@PreAuthorize("hasAuthority('ADMIN')")
public class DiscountController {

    private final DiscountService discountService;

    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    @PostMapping
    public Discount create(@RequestBody Discount discount) {
        return discountService.create(discount);
    }

    @GetMapping
    public List<Discount> getAll() {
        return discountService.getAll();
    }

    @PutMapping("/{id}/deactivate")
    public void deactivate(@PathVariable Long id) {
        discountService.deactivate(id);
    }
}
