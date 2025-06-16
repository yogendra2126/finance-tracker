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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

@Tag(name = "Transactions", description = "Operations related to transactions")
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

    @Operation(
            summary = "Get paginated transactions",
            description = "Returns a paginated list of the authenticated user's transactions, optionally filtered by type, category, or date range."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getTransactions(
            Authentication authentication,
            @Parameter(description = "Transaction type: INCOME or EXPENSE") @RequestParam(required = false) TransactionType type,
            @Parameter(description = "Category ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Start date (yyyy-MM-dd)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable
    ) {
        Long userId = getUserId(authentication);
        Page<Transaction> page = transactionService.getTransactionsWithFilters(
                userId, type, categoryId, startDate, endDate, pageable
        );
        Page<TransactionResponse> dtoPage = page.map(TransactionResponse::fromEntity);
        return ResponseEntity.ok(dtoPage);
    }

    @Operation(
            summary = "Create a new transaction",
            description = "Creates a new transaction for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transaction created", content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody TransactionRequest request,
            Authentication authentication) {
        Long userId = getUserId(authentication);

        // Map TransactionRequest to Transaction entity
        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setType(TransactionType.valueOf(request.getType()));
        transaction.setDescription(request.getDescription());
        transaction.setDate(request.getDate());

        // Category ownership check
        Category category = categoryService.getCategoryByIdAndUserId(request.getCategoryId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found or does not belong to user"));
        transaction.setCategory(category);

        // Create transaction with userId
        Transaction created = transactionService.createTransaction(transaction, userId);

        // Map created Transaction to TransactionResponse DTO
        TransactionResponse response = TransactionResponse.fromEntity(created);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Get a transaction by ID",
            description = "Returns a transaction by its ID if it belongs to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction found", content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(
            @Parameter(description = "Transaction ID") @PathVariable Long id,
            Authentication authentication) {
        Long userId = getUserId(authentication);
        Optional<Transaction> transaction = transactionService.getTransactionById(id, userId);
        return transaction
                .map(tx -> ResponseEntity.ok(TransactionResponse.fromEntity(tx)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Update a transaction",
            description = "Updates an existing transaction for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction updated", content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "404", description = "Transaction or category not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @Parameter(description = "Transaction ID") @PathVariable Long id,
            @Valid @RequestBody TransactionRequest transactionRequest,
            Authentication authentication) {
        Long userId = getUserId(authentication);

        // Map TransactionRequest to Transaction entity
        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setAmount(transactionRequest.getAmount());
        updatedTransaction.setType(TransactionType.valueOf(transactionRequest.getType()));
        updatedTransaction.setDescription(transactionRequest.getDescription());
        updatedTransaction.setDate(transactionRequest.getDate());

        // Category ownership check
        Category category = categoryService.getCategoryByIdAndUserId(transactionRequest.getCategoryId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found or does not belong to user"));
        updatedTransaction.setCategory(category);

        Transaction updated = transactionService.updateTransaction(id, updatedTransaction, userId);
        return ResponseEntity.ok(TransactionResponse.fromEntity(updated));
    }

    @Operation(
            summary = "Delete a transaction",
            description = "Deletes a transaction by its ID if it belongs to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Transaction deleted"),
            @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @Parameter(description = "Transaction ID") @PathVariable Long id,
            Authentication authentication) {
        Long userId = getUserId(authentication);
        transactionService.deleteTransaction(id, userId);
        return ResponseEntity.noContent().build();
    }
}
