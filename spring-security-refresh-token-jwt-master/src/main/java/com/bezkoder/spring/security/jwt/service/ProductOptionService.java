package com.bezkoder.spring.security.jwt.service;

import com.bezkoder.spring.security.jwt.models.Product;
import com.bezkoder.spring.security.jwt.models.ProductOption;
import com.bezkoder.spring.security.jwt.repository.ProductOptionRepository;
import com.bezkoder.spring.security.jwt.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductOptionService {

    private final ProductOptionRepository productOptionRepository;
    private final ProductRepository productRepository;

    public ProductOptionService(ProductOptionRepository productOptionRepository,
                                ProductRepository productRepository) {
        this.productOptionRepository = productOptionRepository;
        this.productRepository = productRepository;
    }

    public List<ProductOption> getByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return productOptionRepository.findByProduct(product);
    }

    public ProductOption getById(Long id) {
        return productOptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Option not found"));
    }

    public ProductOption create(Long productId, ProductOption option) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        option.setProduct(product);
        return productOptionRepository.save(option);
    }

    public ProductOption update(Long id, ProductOption updatedOption) {
        ProductOption option = getById(id);

        option.setOptionType(updatedOption.getOptionType());
        option.setOptionValue(updatedOption.getOptionValue());
        option.setPriceModifier(updatedOption.getPriceModifier());

        return productOptionRepository.save(option);
    }

    public void delete(Long id) {
        productOptionRepository.deleteById(id);
    }
}