package com.bezkoder.spring.security.jwt.service;

import com.bezkoder.spring.security.jwt.models.Discount;
import com.bezkoder.spring.security.jwt.repository.DiscountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscountService {

    private final DiscountRepository discountRepository;

    public DiscountService(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    public Discount create(Discount discount) {
        return discountRepository.save(discount);
    }

    public List<Discount> getAll() {
        return discountRepository.findAll();
    }

    public void deactivate(Long id) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discount not found"));
        discount.setActive(false);
        discountRepository.save(discount);
    }
}
