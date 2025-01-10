package com.trabalho.controlefinancas;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.trabalho.controlefinancas.model.*;
import org.junit.jupiter.api.Test;

class TransactionModelTests {

    @Test
    void construtorTransacao() {
        Category category = new Category("Food");
        User user = new User();
        LocalDate date = LocalDate.now();
        Transaction transaction = new Transaction(
            TransactionType.RECEITA,
            category,
            BigDecimal.valueOf(100.50),
            date,
            "Salario",
            true,
            Currency.BRL,
            user
        );

        assertEquals(TransactionType.RECEITA, transaction.getType());
        assertEquals(category, transaction.getCategory());
        assertEquals(BigDecimal.valueOf(100.50), transaction.getAmount());
        assertEquals(date, transaction.getDate());
        assertEquals("Salario", transaction.getDescription());
        assertTrue(transaction.isRecurring());
        assertEquals(user, transaction.getUser());
    }

    @Test
    void getId() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        assertEquals(1L, transaction.getId());
    }

    @Test
    void setGetDescricao() {
        Transaction transaction = new Transaction();
        transaction.setDescription("Grocery shopping");
        assertEquals("Grocery shopping", transaction.getDescription());
    }

    @Test
    void setEIsRecurring() {
        Transaction transaction = new Transaction();
        transaction.setRecurring(true);
        assertTrue(transaction.isRecurring());

        transaction.setRecurring(false);
        assertFalse(transaction.isRecurring());
    }
}

