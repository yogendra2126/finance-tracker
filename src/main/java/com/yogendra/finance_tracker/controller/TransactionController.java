package com.yogendra.finance_tracker.controller;

import com.yogendra.finance_tracker.dto.TransactionRequest;
import com.yogendra.finance_tracker.dto.TransactionResponse;
import com.yogendra.finance_tracker.model.Category;
import com.yogendra.finance_tracker.model.Transaction;
import com.yogendra.finance_tracker.model.TransactionType;
import com.yogendra.finance_tracker.model.User;
import com.yogendra.finance_tracker.repository.UserRepository;
import com.yogendra.finance_tracker.service.CategoryService;
import com.yogendra.finance_tracker.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final UserRepository userRepository;

    @Autowired
    public TransactionController(TransactionService transactionService, CategoryService categoryService, UserRepository userRepository) {
        this.transactionService = transactionService;
        this.categoryService = categoryService;
        this.userRepository = userRepository;
    }

    // Helper method to get userId from Authentication
    private Long getUserId(Authentication authentication) {
        String username = authentication.getName(); // assuming username is unique
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getTransactions(
            Authentication authentication,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable
    ) {
        Long userId = getUserId(authentication);
        Page<Transaction> page = transactionService.getTransactionsWithFilters(
                userId, type, categoryId, startDate, endDate, pageable
        );
        Page<TransactionResponse> dtoPage = page.map(TransactionResponse::fromEntity);
        return ResponseEntity.ok(dtoPage);
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @RequestBody TransactionRequest request,
            Authentication authentication) {
        Long userId = getUserId(authentication);

        // Map TransactionRequest to Transaction entity
        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setType(TransactionType.valueOf(request.getType()));
        transaction.setDescription(request.getDescription());
        transaction.setDate(request.getDate());

        // Fetch and set Category entity by ID (assume categoryService or repository is available)
        Category category = categoryService.getCategoryById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        transaction.setCategory(category);

        // Create transaction with userId
        Transaction created = transactionService.createTransaction(transaction, userId);

        // Map created Transaction to TransactionResponse DTO
        TransactionResponse response = TransactionResponse.fromEntity(created);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/transactions/{id}
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(
            @PathVariable Long id, Authentication authentication) {
        Long userId = getUserId(authentication);
        Optional<Transaction> transaction = transactionService.getTransactionById(id, userId);
        return transaction
                .map(tx -> ResponseEntity.ok(TransactionResponse.fromEntity(tx)))
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/transactions/{id}
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable Long id,
            @RequestBody TransactionRequest transactionRequest,
            Authentication authentication) {
        Long userId = getUserId(authentication);

        // Map TransactionRequest to Transaction entity
        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setAmount(transactionRequest.getAmount());
        updatedTransaction.setType(TransactionType.valueOf(transactionRequest.getType()));
        updatedTransaction.setDescription(transactionRequest.getDescription());
        updatedTransaction.setDate(transactionRequest.getDate());
        // Fetch and set Category entity by ID (assume categoryService or repository is available)
        Category category = categoryService.getCategoryById(transactionRequest.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        updatedTransaction.setCategory(category);

        Transaction updated = transactionService.updateTransaction(id, updatedTransaction, userId);
        return ResponseEntity.ok(TransactionResponse.fromEntity(updated));
    }

    // DELETE /api/transactions/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Long id, Authentication authentication) {
        Long userId = getUserId(authentication);
        transactionService.deleteTransaction(id, userId);
        return ResponseEntity.noContent().build();
    }

}
