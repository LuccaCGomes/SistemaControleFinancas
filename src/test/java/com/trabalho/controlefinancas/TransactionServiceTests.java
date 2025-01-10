package com.trabalho.controlefinancas;

import com.trabalho.controlefinancas.model.Category;
import com.trabalho.controlefinancas.model.Transaction;
import com.trabalho.controlefinancas.model.TransactionType;
import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.repository.TransactionRepository;
import com.trabalho.controlefinancas.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    @Test
    void getUserTransactions_ValidUser_ReturnsTransactions() {
        User user = new User();
        List<Transaction> transactions = List.of(new Transaction(), new Transaction());

        when(transactionRepository.findByUser(user)).thenReturn(transactions);

        List<Transaction> result = transactionService.getUserTransactions(user);

        assertEquals(2, result.size());
        verify(transactionRepository, times(1)).findByUser(user);
    }

    @Test
    void addTransaction_WithinBudget_ReturnsNull() {
        User user = new User();
        Category category = new Category();
        category.setBudget(new BigDecimal("500"));
        category.setName("Food");

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setAmount(new BigDecimal("200"));
        transaction.setType(TransactionType.DESPESA);
        transaction.setDate(LocalDate.of(2024, 11, 20));

        when(transactionRepository.findByCategoryAndUser(category, user)).thenReturn(List.of(transaction));

        String result = transactionService.addTransaction(transaction);

        assertNull(result);
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    void addTransaction_ExceedsBudget_ReturnsWarningMessage() {
        User user = new User();
        Category category = new Category();
        category.setBudget(new BigDecimal("300"));
        category.setName("Transport");

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setAmount(new BigDecimal("200"));
        transaction.setType(TransactionType.DESPESA);
        transaction.setDate(LocalDate.of(2024, 11, 20));

        Transaction pastTransaction = new Transaction();
        pastTransaction.setAmount(new BigDecimal("150"));
        pastTransaction.setType(TransactionType.DESPESA);
        pastTransaction.setDate(LocalDate.of(2024, 11, 10));

        when(transactionRepository.findByCategoryAndUser(category, user)).thenReturn(List.of(pastTransaction));

        String result = transactionService.addTransaction(transaction);

        assertEquals("O valor das transações para a categoria Transport excedeu o orçamento mensal.", result);
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    void addTransaction_RevenueTransaction_ReturnsNull() {
        User user = new User();
        Category category = new Category();

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setAmount(new BigDecimal("100"));
        transaction.setType(TransactionType.RECEITA);
        transaction.setDate(LocalDate.of(2024, 11, 20));

        String result = transactionService.addTransaction(transaction);

        assertNull(result);
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    void deleteTransactionByIdAndUser_AuthorizedUser_DeletesTransaction() {
        User user = new User();
        user.setId(1L);

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setUser(user);

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        transactionService.deleteTransactionByIdAndUser(1L, user);

        verify(transactionRepository, times(1)).delete(transaction);
    }

    @Test
    void deleteTransactionByIdAndUser_UnauthorizedUser_ThrowsAccessDeniedException() {
        User user = new User();
        user.setId(1L);

        User otherUser = new User();
        otherUser.setId(2L);

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setUser(otherUser);

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        assertThrows(AccessDeniedException.class, () -> transactionService.deleteTransactionByIdAndUser(1L, user));

        verify(transactionRepository, never()).delete(transaction);
    }

    @Test
    void getMonthlyFinancialSummary_ValidUser_CalculatesSummary() {
        User user = new User();
        user.setId(1L);

        Transaction t1 = new Transaction();
        t1.setUser(user);
        t1.setType(TransactionType.RECEITA);
        t1.setDate(LocalDate.of(2023, 10, 1));
        t1.setAmount(new BigDecimal("100"));

        Transaction t2 = new Transaction();
        t2.setUser(user);
        t2.setType(TransactionType.RECEITA);
        t2.setDate(LocalDate.of(2023, 11, 1));
        t2.setAmount(new BigDecimal("200"));

        Transaction t3 = new Transaction();
        t3.setUser(user);
        t3.setType(TransactionType.DESPESA);
        t3.setDate(LocalDate.of(2023, 11, 2));
        t3.setAmount(new BigDecimal("300"));

        when(transactionRepository.findByUser(user)).thenReturn(List.of(t1, t2, t3));

        Map<String, BigDecimal> summary = transactionService.getMonthlyFinancialSummary(user, 11, 2023);

        assertEquals(new BigDecimal("100"), summary.get("initialBalance"));
        assertEquals(new BigDecimal("200"), summary.get("totalIncome"));
        assertEquals(new BigDecimal("300"), summary.get("totalExpense"));
        assertEquals(new BigDecimal("0"), summary.get("finalBalance"));
    }

    @Test
    void findById_ValidTransactionId_ReturnsTransaction() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        Transaction result = transactionService.findById(1L);

        assertEquals(transaction, result);
    }

    @Test
    void findById_InvalidTransactionId_ThrowsException() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> transactionService.findById(1L));
    }

    @Test
    void updateTransaction_ValidTransaction_UpdatesTransaction() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);

        when(transactionRepository.existsById(1L)).thenReturn(true);

        transactionService.updateTransaction(transaction);

        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    void updateTransaction_InvalidTransaction_ThrowsException() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);

        when(transactionRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> transactionService.updateTransaction(transaction));
    }
}