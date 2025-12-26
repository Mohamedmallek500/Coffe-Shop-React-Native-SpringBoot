package com.bezkoder.spring.security.jwt.service;

import com.bezkoder.spring.security.jwt.models.Category;
import com.bezkoder.spring.security.jwt.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Category create(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new RuntimeException("Category already exists");
        }
        return categoryRepository.save(category);
    }

    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}
