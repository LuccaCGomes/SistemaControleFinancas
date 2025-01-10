package com.trabalho.controlefinancas.controller;

import com.trabalho.controlefinancas.exception.BudgetExceededException;
import com.trabalho.controlefinancas.model.Category;
import com.trabalho.controlefinancas.model.Transaction;
import com.trabalho.controlefinancas.model.TransactionType;
import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.repository.CategoryRepository;
import com.trabalho.controlefinancas.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private TransactionController transactionController;

    private User testUser;
    private Category testCategory;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Test Category");

        testTransaction = new Transaction();
        testTransaction.setId(1L);
        testTransaction.setType(TransactionType.DESPESA);
        testTransaction.setAmount(BigDecimal.valueOf(100));
        testTransaction.setCategory(testCategory);
        testTransaction.setUser(testUser);
        testTransaction.setDate(LocalDate.now());
        testTransaction.setDescription("Test Transaction");
    }

    @Test
    void showTransactions_LoadsTransactionsAndCategories_ReturnsTransactionsView() {
        // Given
        List<Transaction> transactions = Arrays.asList(testTransaction);
        List<Category> categories = Arrays.asList(testCategory);
        when(transactionService.getUserTransactions(testUser)).thenReturn(transactions);
        when(categoryRepository.findByUser(testUser)).thenReturn(categories);

        // When
        String viewName = transactionController.showTransactions(model, testUser);

        // Then
        assertEquals("transactions", viewName);
        verify(model).addAttribute("transactions", transactions);
        verify(model).addAttribute("categories", categories);
        verify(model).addAttribute("totalValue", BigDecimal.valueOf(100));
    }

    @Test
    void showAddTransactionForm_LoadsCategories_ReturnsAddTransactionView() {
        // Given
        List<Category> categories = Arrays.asList(testCategory);
        when(categoryRepository.findByUser(testUser)).thenReturn(categories);

        // When
        String viewName = transactionController.showAddTransactionForm(model, testUser);

        // Then
        assertEquals("add-transaction", viewName);
        verify(model).addAttribute("categories", categories);
    }

    @Test
    void addTransaction_SuccessfulTransaction_RedirectsToTransactions() {
        // Given
        when(transactionService.addTransaction(any(Transaction.class))).thenReturn(null);

        // When
        String viewName = transactionController.addTransaction(
                "DESPESA",
                testCategory,
                BigDecimal.valueOf(100),
                LocalDate.now().toString(),
                "Test Description",
                false,
                testUser,
                redirectAttributes
        );

        // Then
        assertEquals("redirect:/transactions", viewName);
        verify(redirectAttributes).addFlashAttribute("message", "Transação adicionada com sucesso!");
    }

    @Test
    void addTransaction_BudgetWarning_RedirectsWithWarning() {
        // Given
        String warningMessage = "Orçamento excedido";
        when(transactionService.addTransaction(any(Transaction.class))).thenReturn(warningMessage);

        // When
        String viewName = transactionController.addTransaction(
                "DESPESA",
                testCategory,
                BigDecimal.valueOf(100),
                LocalDate.now().toString(),
                "Test Description",
                false,
                testUser,
                redirectAttributes
        );

        // Then
        assertEquals("redirect:/transactions", viewName);
        verify(redirectAttributes).addFlashAttribute("error", warningMessage);
    }

    @Test
    void addTransaction_BudgetExceeded_RedirectsWithError() {
        // Given
        when(transactionService.addTransaction(any(Transaction.class)))
                .thenThrow(new BudgetExceededException("Orçamento excedido"));

        // When
        String viewName = transactionController.addTransaction(
                "DESPESA",
                testCategory,
                BigDecimal.valueOf(100),
                LocalDate.now().toString(),
                "Test Description",
                false,
                testUser,
                redirectAttributes
        );

        // Then
        assertEquals("redirect:/transactions", viewName);
        verify(redirectAttributes).addFlashAttribute("error", "Orçamento excedido");
    }

    @Test
    void deleteTransaction_ValidTransaction_RedirectsToTransactions() {
        // When
        String viewName = transactionController.deleteTransaction(1L, testUser);

        // Then
        assertEquals("redirect:/transactions", viewName);
        verify(transactionService).deleteTransactionByIdAndUser(1L, testUser);
    }

    @Test
    void editTransaction_ValidTransaction_RedirectsToTransactions() {
        // Given
        when(transactionService.findById(1L)).thenReturn(testTransaction);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        // When
        String viewName = transactionController.editTransaction(
                "DESPESA",
                1L,
                1L,
                LocalDate.now().toString(),
                "Updated Description",
                BigDecimal.valueOf(150),
                redirectAttributes
        );

        // Then
        assertEquals("redirect:/transactions", viewName);
        verify(transactionService).updateTransaction(any(Transaction.class));
        verify(redirectAttributes).addFlashAttribute("message", "Transação editada com sucesso!");
    }

    @Test
    void editTransaction_InvalidCategory_ThrowsException() {
        // Given
        when(transactionService.findById(1L)).thenReturn(testTransaction);
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // When/Then
        try {
            transactionController.editTransaction(
                    "DESPESA",
                    1L,
                    1L,
                    LocalDate.now().toString(),
                    "Updated Description",
                    BigDecimal.valueOf(150),
                    redirectAttributes
            );
        } catch (IllegalArgumentException e) {
            assertEquals("Categoria não encontrada", e.getMessage());
        }
    }

    @Test
    void addTransaction_RecurringTransaction_SavesWithRecurringFlag() {
        // Given
        when(transactionService.addTransaction(any(Transaction.class))).thenReturn(null);

        // When
        String viewName = transactionController.addTransaction(
                "DESPESA",
                testCategory,
                BigDecimal.valueOf(100),
                LocalDate.now().toString(),
                "Test Description",
                true, // recurring flag set to true
                testUser,
                redirectAttributes
        );

        // Then
        assertEquals("redirect:/transactions", viewName);
        verify(transactionService).addTransaction(argThat(transaction ->
                transaction.isRecurring() &&
                        transaction.getType() == TransactionType.DESPESA &&
                        transaction.getAmount().equals(BigDecimal.valueOf(100))
        ));
    }
}