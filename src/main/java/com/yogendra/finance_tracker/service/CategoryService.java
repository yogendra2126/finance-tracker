package com.yogendra.finance_tracker.service;

import com.yogendra.finance_tracker.dto.CategoryRequest;
import com.yogendra.finance_tracker.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    Category createCategory(CategoryRequest request, Long userId);
    List<Category> getCategoriesByUserId(Long userId);
    Optional<Category> getCategoryByIdAndUserId(Long id, Long userId);
    Category updateCategory(Long id, CategoryRequest request, Long userId);
    void deleteCategory(Long id, Long userId);
}
