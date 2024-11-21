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
    void testGetUserTransactions() {
        User user = new User();
        Transaction transaction1 = new Transaction();
        Transaction transaction2 = new Transaction();
        List<Transaction> transactions = List.of(transaction1, transaction2);

        when(transactionRepository.findByUser(user)).thenReturn(transactions);

        List<Transaction> result = transactionService.getUserTransactions(user);

        assertEquals(2, result.size());
        verify(transactionRepository, times(1)).findByUser(user);
    }

    @Test
    void testAddTransactionWithinBudget() {
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
    void testAddTransactionExceedsBudget() {
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
    void testDeleteTransactionByIdAndUserAuthorized() {
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
    void testDeleteTransactionByIdAndUserUnauthorized() {
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
    void testGetMonthlyFinancialSummary() {
        // Mock transactions
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

}
