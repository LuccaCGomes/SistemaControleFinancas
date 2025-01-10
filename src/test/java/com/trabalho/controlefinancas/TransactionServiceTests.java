package com.trabalho.controlefinancas.service;

import com.trabalho.controlefinancas.model.Category;
import com.trabalho.controlefinancas.model.Transaction;
import com.trabalho.controlefinancas.model.TransactionType;
import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User user;
    private Category category;
    private Transaction transaction;
    private LocalDate currentDate;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        category.setBudget(new BigDecimal("1000.00"));

        currentDate = LocalDate.of(2024, 1, 15);

        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setAmount(new BigDecimal("500.00"));
        transaction.setDate(currentDate);
        transaction.setType(TransactionType.DESPESA);
    }

    @Test
    void getUserTransactions_ShouldReturnUserTransactions() {
        // Arrange
        List<Transaction> expectedTransactions = Arrays.asList(transaction);
        when(transactionRepository.findByUser(user)).thenReturn(expectedTransactions);

        // Act
        List<Transaction> result = transactionService.getUserTransactions(user);

        // Assert
        assertEquals(expectedTransactions, result);
        verify(transactionRepository).findByUser(user);
    }

    @Test
    void addTransaction_WithRevenue_ShouldSaveWithoutBudgetCheck() {
        // Arrange
        transaction.setType(TransactionType.RECEITA);

        // Act
        String result = transactionService.addTransaction(transaction);

        // Assert
        assertNull(result);
        verify(transactionRepository).save(transaction);
        verify(transactionRepository, never()).findByCategoryAndUser(any(), any());
    }

    @Test
    void addTransaction_WithNullBudget_ShouldSaveWithoutBudgetCheck() {
        // Arrange
        category.setBudget(null);

        // Act
        String result = transactionService.addTransaction(transaction);

        // Assert
        assertNull(result);
        verify(transactionRepository).save(transaction);
        verify(transactionRepository, never()).findByCategoryAndUser(any(), any());
    }

    @Test
    void addTransaction_WithinBudget_ShouldSaveSuccessfully() {
        // Arrange
        List<Transaction> existingTransactions = Arrays.asList(
                createTransaction(new BigDecimal("300.00"))
        );
        when(transactionRepository.findByCategoryAndUser(category, user))
                .thenReturn(existingTransactions);

        // Act
        String result = transactionService.addTransaction(transaction);

        // Assert
        assertNull(result);
        verify(transactionRepository).save(transaction);
    }

    @Test
    void addTransaction_ExceedingBudget_ShouldSaveWithWarning() {
        // Arrange
        List<Transaction> existingTransactions = Arrays.asList(
                createTransaction(new BigDecimal("700.00"))
        );
        when(transactionRepository.findByCategoryAndUser(category, user))
                .thenReturn(existingTransactions);

        // Act
        String result = transactionService.addTransaction(transaction);

        // Assert
        assertEquals("O valor das transações para a categoria Test Category excedeu o orçamento mensal.", result);
        verify(transactionRepository).save(transaction);
    }

    @Test
    void deleteTransactionByIdAndUser_WithAuthorizedUser_ShouldDelete() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        // Act
        transactionService.deleteTransactionByIdAndUser(1L, user);

        // Assert
        verify(transactionRepository).delete(transaction);
    }

    @Test
    void deleteTransactionByIdAndUser_WithUnauthorizedUser_ShouldThrowException() {
        // Arrange
        User unauthorizedUser = new User();
        unauthorizedUser.setId(2L);
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        // Act & Assert
        assertThrows(AccessDeniedException.class,
                () -> transactionService.deleteTransactionByIdAndUser(1L, unauthorizedUser));
        verify(transactionRepository, never()).delete(any());
    }

    @Test
    void deleteTransactionByIdAndUser_WithNonexistentTransaction_ShouldThrowException() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> transactionService.deleteTransactionByIdAndUser(1L, user));
        verify(transactionRepository, never()).delete(any());
    }

    @Test
    void getMonthlyFinancialSummary_ShouldCalculateCorrectly() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(
                createTransaction(new BigDecimal("1000.00"), TransactionType.RECEITA, LocalDate.of(2023, 12, 1)),
                createTransaction(new BigDecimal("500.00"), TransactionType.DESPESA, LocalDate.of(2024, 1, 1)),
                createTransaction(new BigDecimal("800.00"), TransactionType.RECEITA, LocalDate.of(2024, 1, 15)),
                createTransaction(new BigDecimal("300.00"), TransactionType.DESPESA, LocalDate.of(2024, 1, 20))
        );
        when(transactionRepository.findByUser(user)).thenReturn(transactions);

        // Act
        Map<String, BigDecimal> result = transactionService.getMonthlyFinancialSummary(user, 1, 2024);

        // Assert
        assertEquals(new BigDecimal("1000.00"), result.get("initialBalance"));
        assertEquals(new BigDecimal("800.00"), result.get("totalIncome"));
        assertEquals(new BigDecimal("800.00"), result.get("totalExpense"));
        assertEquals(new BigDecimal("1000.00"), result.get("finalBalance"));
    }

    @Test
    void findById_WithExistingTransaction_ShouldReturnTransaction() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        // Act
        Transaction result = transactionService.findById(1L);

        // Assert
        assertEquals(transaction, result);
    }

    @Test
    void findById_WithNonexistentTransaction_ShouldThrowException() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transactionService.findById(1L));
        assertEquals("Transação não encontrada com o ID: 1", exception.getMessage());
    }

    @Test
    void updateTransaction_WithExistingTransaction_ShouldUpdate() {
        // Arrange
        when(transactionRepository.existsById(1L)).thenReturn(true);

        // Act
        transactionService.updateTransaction(transaction);

        // Assert
        verify(transactionRepository).save(transaction);
    }

    @Test
    void updateTransaction_WithNonexistentTransaction_ShouldThrowException() {
        // Arrange
        when(transactionRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transactionService.updateTransaction(transaction));
        assertEquals("Transação não encontrada com o ID: 1", exception.getMessage());
    }

    @Test
    void findByMonth_ShouldReturnTransactionsForMonth() {
        // Arrange
        List<Transaction> expectedTransactions = Arrays.asList(transaction);
        when(transactionRepository.findTransactionsByMonthAndUser(2024, 1, user))
                .thenReturn(expectedTransactions);

        // Act
        List<Transaction> result = transactionService.findByMonth(2024, 1, user);

        // Assert
        assertEquals(expectedTransactions, result);
        verify(transactionRepository).findTransactionsByMonthAndUser(2024, 1, user);
    }

    private Transaction createTransaction(BigDecimal amount) {
        return createTransaction(amount, TransactionType.DESPESA, currentDate);
    }

    private Transaction createTransaction(BigDecimal amount, TransactionType type, LocalDate date) {
        Transaction t = new Transaction();
        t.setUser(user);
        t.setCategory(category);
        t.setAmount(amount);
        t.setType(type);
        t.setDate(date);
        return t;
    }
}