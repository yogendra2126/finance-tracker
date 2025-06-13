package com.yogendra.finance_tracker.dto;

import com.yogendra.finance_tracker.model.Transaction;
import com.yogendra.finance_tracker.model.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
public class TransactionResponse {
    private Long id;
    private BigDecimal amount;
    private TransactionType type;
    private String description;
    private LocalDate date;
    private Long categoryId;
    private String categoryName;

    // Constructors
    public TransactionResponse() {}

    public TransactionResponse(Long id, BigDecimal amount, TransactionType type, String description,
                               LocalDate date, Long categoryId, String categoryName) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.date = date;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    // Static factory method to convert from entity
    public static TransactionResponse fromEntity(Transaction tx) {
        return new TransactionResponse(
                tx.getId(),
                tx.getAmount(),
                tx.getType(),
                tx.getDescription(),
                tx.getDate(),
                tx.getCategory() != null ? tx.getCategory().getId() : null,
                tx.getCategory() != null ? tx.getCategory().getName() : null
        );
    }
}
