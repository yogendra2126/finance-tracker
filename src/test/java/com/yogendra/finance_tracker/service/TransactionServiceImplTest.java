package com.yogendra.finance_tracker.service;

import com.yogendra.finance_tracker.model.Transaction;
import com.yogendra.finance_tracker.model.TransactionType;
import com.yogendra.finance_tracker.model.User;
import com.yogendra.finance_tracker.repository.TransactionRepository;
import com.yogendra.finance_tracker.repository.UserRepository;
import com.yogendra.finance_tracker.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks before each test[3]
    }

    @Test
    void createTransaction_shouldSaveTransactionWithUser() {
        User user = new User();
        user.setId(1L);

        Transaction tx = new Transaction();
        tx.setAmount(BigDecimal.valueOf(100));
        tx.setType(TransactionType.INCOME);
        tx.setDate(LocalDate.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        Transaction saved = transactionService.createTransaction(tx, 1L);

        assertEquals(user, saved.getUser());
        verify(transactionRepository, times(1)).save(tx);
    }

    @Test
    void getTransactionById_shouldReturnTransactionIfUserOwnsIt() {
        User user = new User();
        user.setId(1L);

        Transaction tx = new Transaction();
        tx.setId(10L);
        tx.setUser(user);

        when(transactionRepository.findById(10L)).thenReturn(Optional.of(tx));

        Optional<Transaction> result = transactionService.getTransactionById(10L, 1L);

        assertTrue(result.isPresent());
        assertEquals(10L, result.get().getId());
    }

    @Test
    void getTransactionById_shouldReturnEmptyIfUserDoesNotOwnIt() {
        User user = new User();
        user.setId(2L);

        Transaction tx = new Transaction();
        tx.setId(10L);
        tx.setUser(user);

        when(transactionRepository.findById(10L)).thenReturn(Optional.of(tx));

        Optional<Transaction> result = transactionService.getTransactionById(10L, 1L);

        assertFalse(result.isPresent());
    }

    @Test
    void updateTransaction_shouldThrowIfTransactionNotFound() {
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.updateTransaction(99L, new Transaction(), 1L);
        });
    }

    @Test
    void updateTransaction_shouldThrowIfUserNotOwner() {
        User user = new User();
        user.setId(2L);

        Transaction tx = new Transaction();
        tx.setId(10L);
        tx.setUser(user);

        when(transactionRepository.findById(10L)).thenReturn(Optional.of(tx));

        assertThrows(SecurityException.class, () -> {
            transactionService.updateTransaction(10L, new Transaction(), 1L);
        });
    }

    @Test
    void deleteTransaction_shouldThrowIfTransactionNotFound() {
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.deleteTransaction(99L, 1L);
        });
    }

    @Test
    void deleteTransaction_shouldThrowIfUserNotOwner() {
        User user = new User();
        user.setId(2L);

        Transaction tx = new Transaction();
        tx.setId(10L);
        tx.setUser(user);

        when(transactionRepository.findById(10L)).thenReturn(Optional.of(tx));

        assertThrows(SecurityException.class, () -> {
            transactionService.deleteTransaction(10L, 1L);
        });
    }
}
