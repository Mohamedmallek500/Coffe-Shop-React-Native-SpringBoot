package com.bezkoder.spring.security.jwt.controllers;

import com.bezkoder.spring.security.jwt.models.Category;
import com.bezkoder.spring.security.jwt.service.CategoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // PUBLIC
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAll();
    }

    @GetMapping("/{id}")
    public Category getCategory(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    // ADMIN ONLY
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        return categoryService.create(category);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
    }
}
