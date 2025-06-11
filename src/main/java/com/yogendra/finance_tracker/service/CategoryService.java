package com.yogendra.finance_tracker.service;

import com.yogendra.finance_tracker.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryService {
    Category createCategory(Category category);
    List<Category> getCategoriesByUserId(Long userId);
    Optional<Category> getCategoryById(Long id);
    Category updateCategory(Long id, Category categoryDetails);
    void deleteCategory(Long id);
}
