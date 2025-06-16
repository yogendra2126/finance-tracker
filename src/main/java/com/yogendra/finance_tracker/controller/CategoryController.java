package com.yogendra.finance_tracker.controller;

import com.yogendra.finance_tracker.dto.CategoryRequest;
import com.yogendra.finance_tracker.dto.CategoryResponse;
import com.yogendra.finance_tracker.model.Category;
import com.yogendra.finance_tracker.model.User;
import com.yogendra.finance_tracker.repository.UserRepository;
import com.yogendra.finance_tracker.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Categories", description = "Operations related to categories")
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final UserRepository userRepository;

    @Autowired
    public CategoryController(CategoryService categoryService, UserRepository userRepository) {
        this.categoryService = categoryService;
        this.userRepository = userRepository;
    }

    private Long getUserId(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    @Operation(summary = "Create a new category")
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request,
            Authentication authentication) {
        Long userId = getUserId(authentication);
        Category created = categoryService.createCategory(request, userId);
        return ResponseEntity.ok(CategoryResponse.fromEntity(created));
    }

    @Operation(summary = "Get all categories for the authenticated user")
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories(Authentication authentication) {
        Long userId = getUserId(authentication);
        List<Category> categories = categoryService.getCategoriesByUserId(userId);
        List<CategoryResponse> responses = categories.stream()
                .map(CategoryResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get a category by ID (if owned by user)")
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = getUserId(authentication);
        Category category = categoryService.getCategoryByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Category not found or does not belong to user"));
        return ResponseEntity.ok(CategoryResponse.fromEntity(category));
    }

    @Operation(summary = "Update a category (if owned by user)")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request,
            Authentication authentication) {
        Long userId = getUserId(authentication);
        Category updated = categoryService.updateCategory(id, request, userId);
        return ResponseEntity.ok(CategoryResponse.fromEntity(updated));
    }

    @Operation(summary = "Delete a category (if owned by user)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = getUserId(authentication);
        categoryService.deleteCategory(id, userId);
        return ResponseEntity.noContent().build();
    }
}
