package com.trabalho.controlefinancas;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.trabalho.controlefinancas.model.Category;
import com.trabalho.controlefinancas.model.Transaction;
import com.trabalho.controlefinancas.model.User;

class CategoryModelTests {

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category("Food", "Expenses related to food", BigDecimal.valueOf(500.00));
    }

    @Test
    void construtorDefault() {
        Category defaultCategory = new Category();
        assertNull(defaultCategory.getName());
        assertNull(defaultCategory.getDescription());
        assertNull(defaultCategory.getBudget());
        assertNotNull(defaultCategory.getTransactions());
        assertTrue(defaultCategory.getTransactions().isEmpty());
    }

    @Test
    void construtorComNome() {
        Category nameOnlyCategory = new Category("Viagem");
        assertEquals("Viagem", nameOnlyCategory.getName());
        assertNull(nameOnlyCategory.getDescription());
        assertNull(nameOnlyCategory.getBudget());
    }

    @Test
    void construtorComNomeEOrcamento() {
        Category budgetCategory = new Category("Viagem", BigDecimal.valueOf(300.00));
        assertEquals("Viagem", budgetCategory.getName());
        assertNull(budgetCategory.getDescription());
        assertEquals(BigDecimal.valueOf(300.00), budgetCategory.getBudget());
    }

    @Test
    void construtorComNomeEDescricao() {
        Category descriptionCategory = new Category("Viagem", "Gastos com viagens");
        assertEquals("Viagem", descriptionCategory.getName());
        assertEquals("Gastos com viagens", descriptionCategory.getDescription());
        assertNull(descriptionCategory.getBudget());
    }

    @Test
    void construtorCompleto() {
        User user = new User();
        Category fullCategory = new Category("Viagem", "Gastos com viagens", BigDecimal.valueOf(300.00), user);

        assertEquals("Viagem", fullCategory.getName());
        assertEquals("Gastos com viagens", fullCategory.getDescription());
        assertEquals(BigDecimal.valueOf(300.00), fullCategory.getBudget());
        assertEquals(user, fullCategory.getUser());
    }

    @Test
    void testAddTransaction() {
        Transaction transaction = new Transaction();
        category.addTransaction(transaction);

        List<Transaction> transactions = category.getTransactions();
        assertEquals(1, transactions.size());
        assertTrue(transactions.contains(transaction));
        assertEquals(category, transaction.getCategory());
    }

    @Test
    void testRemoveTransaction() {
        Transaction transaction = new Transaction();
        category.addTransaction(transaction);
        category.removeTransaction(transaction);

        List<Transaction> transactions = category.getTransactions();
        assertTrue(transactions.isEmpty());
        assertNull(transaction.getCategory());
    }

    @Test
    void testSettersAndGetters() {
        category.setId(1L);
        category.setName("Entertainment");
        category.setDescription("Movies, games, etc.");
        category.setBudget(BigDecimal.valueOf(100.00));

        assertEquals(1L, category.getId());
        assertEquals("Entertainment", category.getName());
        assertEquals("Movies, games, etc.", category.getDescription());
        assertEquals(BigDecimal.valueOf(100.00), category.getBudget());
    }

    @Test
    void testTransactionListInitialization() {
        assertNotNull(category.getTransactions());
        assertTrue(category.getTransactions().isEmpty());
    }

    @Test
    void testUserAssociation() {
        User user = new User();
        category.setUser(user);

        assertEquals(user, category.getUser());
    }
}
