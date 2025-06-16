package com.yogendra.finance_tracker.service.impl;

import com.yogendra.finance_tracker.dto.CategoryRequest;
import com.yogendra.finance_tracker.model.Category;
import com.yogendra.finance_tracker.model.User;
import com.yogendra.finance_tracker.repository.CategoryRepository;
import com.yogendra.finance_tracker.repository.UserRepository;
import com.yogendra.finance_tracker.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Category createCategory(CategoryRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Category category = new Category();
        category.setName(request.getName());
        category.setUser(user);
        return categoryRepository.save(category);
    }

    @Override
    public List<Category> getCategoriesByUserId(Long userId) {
        return categoryRepository.findByUserId(userId);
    }

    @Override
    public Optional<Category> getCategoryByIdAndUserId(Long id, Long userId) {
        return categoryRepository.findByIdAndUserId(id, userId);
    }

    @Override
    public Category updateCategory(Long id, CategoryRequest request, Long userId) {
        Category category = categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found or does not belong to user"));
        category.setName(request.getName());
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id, Long userId) {
        Category category = categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found or does not belong to user"));
        categoryRepository.delete(category);
    }
}
