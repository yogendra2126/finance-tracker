package com.yogendra.finance_tracker.service;

import com.yogendra.finance_tracker.model.Transaction;
import java.util.List;
import java.util.Optional;

public interface TransactionService {
    Transaction createTransaction(Transaction transaction);
    List<Transaction> getTransactionsByUserId(Long userId);
    Optional<Transaction> getTransactionById(Long id);
    Transaction updateTransaction(Long id, Transaction transactionDetails);
    void deleteTransaction(Long id);
}
