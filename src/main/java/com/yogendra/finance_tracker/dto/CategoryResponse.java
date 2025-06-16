package com.yogendra.finance_tracker.dto;

import com.yogendra.finance_tracker.model.Category;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CategoryResponse {
    private Long id;
    private String name;

    public static CategoryResponse fromEntity(Category category) {
        CategoryResponse dto = new CategoryResponse();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }
}
