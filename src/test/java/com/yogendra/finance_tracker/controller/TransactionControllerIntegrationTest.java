package com.yogendra.finance_tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yogendra.finance_tracker.model.Category;
import com.yogendra.finance_tracker.model.Transaction;
import com.yogendra.finance_tracker.model.TransactionType;
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

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerIntegrationTest {

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
    private Long transactionId;

    @BeforeEach
    void setup() {
        transactionRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();

        User user = new User();
        user.setEmail(TEST_EMAIL);
        user.setName("Test User");
        user.setPassword("password");
        userRepository.save(user);

        Category category = new Category();
        category.setName("Salary");
        categoryRepository.save(category);
        categoryId = category.getId();

        // Insert a transaction for GET/PUT/DELETE tests
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(1000));
        transaction.setType(TransactionType.INCOME);
        transaction.setDescription("Initial Salary");
        transaction.setDate(LocalDate.of(2025, 6, 1));
        transaction.setCategory(category);
        transaction.setUser(user);
        transactionRepository.save(transaction);
        transactionId = transaction.getId();
    }

    @Test
    void getTransactions_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/transactions")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", TEST_EMAIL)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void createTransaction_shouldReturnCreated() throws Exception {
        String txJson = """
        {
            "amount": 500,
            "type": "INCOME",
            "description": "Salary for June",
            "date": "2025-06-13",
            "categoryId": %d
        }
        """.formatted(categoryId);

        mockMvc.perform(post("/api/transactions")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", TEST_EMAIL)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(txJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(500))
                .andExpect(jsonPath("$.type").value("INCOME"))
                .andExpect(jsonPath("$.categoryId").value(categoryId));
    }

    @Test
    void getTransactionById_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/transactions/{id}", transactionId)
                        .with(jwt().jwt(jwt -> jwt.claim("sub", TEST_EMAIL)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId))
                .andExpect(jsonPath("$.amount").value(1000))
                .andExpect(jsonPath("$.type").value("INCOME"));
    }

    @Test
    void updateTransaction_shouldReturnOk() throws Exception {
        String updateJson = """
        {
            "amount": 1200,
            "type": "INCOME",
            "description": "Updated Salary",
            "date": "2025-06-15",
            "categoryId": %d
        }
        """.formatted(categoryId);

        mockMvc.perform(put("/api/transactions/{id}", transactionId)
                        .with(jwt().jwt(jwt -> jwt.claim("sub", TEST_EMAIL)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId))
                .andExpect(jsonPath("$.amount").value(1200))
                .andExpect(jsonPath("$.description").value("Updated Salary"));
    }

    @Test
    void deleteTransaction_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/transactions/{id}", transactionId)
                        .with(jwt().jwt(jwt -> jwt.claim("sub", TEST_EMAIL))))
                .andExpect(status().isNoContent());
    }
}
