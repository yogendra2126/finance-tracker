package com.yogendra.finance_tracker.service.impl;

import com.yogendra.finance_tracker.model.Transaction;
import com.yogendra.finance_tracker.model.TransactionType;
import com.yogendra.finance_tracker.model.User;
import com.yogendra.finance_tracker.repository.TransactionRepository;
import com.yogendra.finance_tracker.repository.UserRepository;
import com.yogendra.finance_tracker.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Transaction createTransaction(Transaction transaction, Long userId) {
        // Fetch user and set ownership
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        transaction.setUser(user);
        return transactionRepository.save(transaction);
    }

    @Override
    public Optional<Transaction> getTransactionById(Long id, Long userId) {
        Optional<Transaction> transactionOpt = transactionRepository.findById(id);
        if (transactionOpt.isPresent() && transactionOpt.get().getUser().getId().equals(userId)) {
            return transactionOpt;
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Transaction updateTransaction(Long id, Transaction transactionDetails, Long userId) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with id: " + id));
        if (!transaction.getUser().getId().equals(userId)) {
            throw new SecurityException("Unauthorized to update this transaction");
        }
        transaction.setAmount(transactionDetails.getAmount());
        transaction.setType(transactionDetails.getType());
        transaction.setDescription(transactionDetails.getDescription());
        transaction.setDate(transactionDetails.getDate());
        transaction.setCategory(transactionDetails.getCategory());
        return transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public void deleteTransaction(Long id, Long userId) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with id: " + id));
        if (!transaction.getUser().getId().equals(userId)) {
            throw new SecurityException("Unauthorized to delete this transaction");
        }
        transactionRepository.deleteById(id);
    }

    @Override
    public Page<Transaction> getTransactionsWithFilters(
            Long userId,
            TransactionType type,
            Long categoryId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        // Handle all filter combinations
        if (type != null && categoryId != null && startDate != null && endDate != null) {
            return transactionRepository.findByUserIdAndTypeAndCategoryIdAndDateBetween(userId, type, categoryId, startDate, endDate, pageable);
        } else if (type != null && categoryId != null) {
            return transactionRepository.
                    findByUserIdAndTypeAndCategoryId(userId, type, categoryId, pageable);
        } else if (type != null && startDate != null && endDate != null) {
            return transactionRepository.findByUserIdAndTypeAndDateBetween(userId, type, startDate, endDate, pageable);
        } else if (categoryId != null && startDate != null && endDate != null) {
            return transactionRepository.findByUserIdAndCategoryIdAndDateBetween(userId, categoryId, startDate, endDate, pageable);
        } else if (type != null) {
            return transactionRepository.findByUserIdAndType(userId, type, pageable);
        } else if (categoryId != null) {
            return transactionRepository.findByUserIdAndCategoryId(userId, categoryId, pageable);
        } else if (startDate != null && endDate != null) {
            return transactionRepository.findByUserIdAndDateBetween(userId, startDate, endDate, pageable);
        } else {
            return transactionRepository.findByUserId(userId, pageable);
        }
    }

}
