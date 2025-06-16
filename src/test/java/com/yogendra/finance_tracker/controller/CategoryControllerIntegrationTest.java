package com.yogendra.finance_tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yogendra.finance_tracker.dto.CategoryRequest;
import com.yogendra.finance_tracker.dto.CategoryResponse;
import com.yogendra.finance_tracker.model.Category;
import com.yogendra.finance_tracker.model.User;
import com.yogendra.finance_tracker.repository.CategoryRepository;
import com.yogendra.finance_tracker.repository.TransactionRepository;
import com.yogendra.finance_tracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String TEST_EMAIL = "user@example.com";
    private Long categoryId;

    @BeforeEach
    void setup() throws Exception {
        // Delete in correct order to avoid foreign key constraint issues
        transactionRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setEmail(TEST_EMAIL);
        user.setName("Test User");
        user.setPassword("password");
        userRepository.save(user);

        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setName("Salary");

        String categoryJson = objectMapper.writeValueAsString(categoryRequest);

        String response = mockMvc.perform(post("/api/categories")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", TEST_EMAIL)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // If your CategoryResponse has an id field, parse it here:
        CategoryResponse category = objectMapper.readValue(response, CategoryResponse.class);
        categoryId = category.getId();
    }

    @Test
    void createCategory_shouldReturnOk() throws Exception {
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setName("Bonus");

        String categoryJson = objectMapper.writeValueAsString(categoryRequest);

        mockMvc.perform(post("/api/categories")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", TEST_EMAIL)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bonus"));
    }

    @Test
    void getCategories_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/categories")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", TEST_EMAIL)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Salary"));
    }

    @Test
    void getCategoryById_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/categories/{id}", categoryId)
                        .with(jwt().jwt(jwt -> jwt.claim("sub", TEST_EMAIL)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryId));
    }

    @Test
    void updateCategory_shouldReturnOk() throws Exception {
        CategoryRequest updateRequest = new CategoryRequest();
        updateRequest.setName("Updated Salary");

        String updateJson = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(put("/api/categories/{id}", categoryId)
                        .with(jwt().jwt(jwt -> jwt.claim("sub", TEST_EMAIL)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Salary"));
    }

    @Test
    void deleteCategory_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/categories/{id}", categoryId)
                        .with(jwt().jwt(jwt -> jwt.claim("sub", TEST_EMAIL))))
                .andExpect(status().isNoContent());
    }
}
