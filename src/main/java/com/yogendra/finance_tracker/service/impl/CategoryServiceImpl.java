package com.yogendra.finance_tracker.service.impl;

import com.yogendra.finance_tracker.model.Category;
import com.yogendra.finance_tracker.repository.CategoryRepository;
import com.yogendra.finance_tracker.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public List<Category> getCategoriesByUserId(Long userId) {
        return categoryRepository.findByUserId(userId);
    }

    @Override
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));
        category.setName(categoryDetails.getName());
        category.setColor(categoryDetails.getColor());
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
