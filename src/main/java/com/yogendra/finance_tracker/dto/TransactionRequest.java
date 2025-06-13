package com.yogendra.finance_tracker.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
public class TransactionRequest {
    private BigDecimal amount;
    private String type; // "INCOME" or "EXPENSE"
    private String description;
    private LocalDate date;
    private Long categoryId;

    // Default constructor
    public TransactionRequest() {}

    // Parameterized constructor
    public TransactionRequest(BigDecimal amount, String type, String description, LocalDate date, Long categoryId) {
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.date = date;
        this.categoryId = categoryId;
    }

}
