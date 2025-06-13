package com.yogendra.finance_tracker.service;

import com.yogendra.finance_tracker.model.Transaction;
import com.yogendra.finance_tracker.model.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

public interface TransactionService {
    Transaction createTransaction(Transaction transaction, Long userId);

    Optional<Transaction> getTransactionById(Long id, Long userId);
    Transaction updateTransaction(Long id, Transaction transactionDetails, Long userId);
    void deleteTransaction(Long id, Long userId);
    Page<Transaction> getTransactionsWithFilters(
            Long userId,
            TransactionType type,
            Long categoryId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );
}
