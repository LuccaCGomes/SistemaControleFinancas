package com.trabalho.controlefinancas;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.trabalho.controlefinancas.model.*;
import com.trabalho.controlefinancas.service.ChartService;
import com.trabalho.controlefinancas.service.TransactionService;
import org.jfree.chart.JFreeChart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;


class ChartServiceTests {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private ChartService chartService;

    private User user;
    private Category category1;
    private Category category2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);

        category1 = new Category();
        category1.setName("Alimentação");

        category2 = new Category();
        category2.setName("Transporte");
    }

    @Test
    void criaGraficoTortaDespesas() {
        // Mock de transações
        Transaction transaction1 = new Transaction();
        transaction1.setType(TransactionType.DESPESA);
        transaction1.setCategory(category1);
        transaction1.setAmount(new BigDecimal("100"));

        Transaction transaction2 = new Transaction();
        transaction2.setType(TransactionType.DESPESA);
        transaction2.setCategory(category1);
        transaction2.setAmount(new BigDecimal("50"));

        Transaction transaction3 = new Transaction();
        transaction3.setType(TransactionType.DESPESA);
        transaction3.setCategory(category2);
        transaction3.setAmount(new BigDecimal("30"));

        when(transactionService.getUserTransactions(user)).thenReturn(Arrays.asList(transaction1, transaction2, transaction3));

        JFreeChart chart = chartService.createExpensePieChart(user);

        assertNotNull(chart);
        assertEquals("Distribuição de Despesas", chart.getTitle().getText());
        verify(transactionService, times(1)).getUserTransactions(user);
    }

    @Test
    void criaGraficoFluxo() {
        Transaction transaction1 = new Transaction();
        transaction1.setType(TransactionType.RECEITA);
        transaction1.setAmount(new BigDecimal("500"));
        transaction1.setDate(LocalDate.of(2024, 11, 1));

        Transaction transaction2 = new Transaction();
        transaction2.setType(TransactionType.DESPESA);
        transaction2.setAmount(new BigDecimal("200"));
        transaction2.setDate(LocalDate.of(2024, 11, 5));

        Transaction transaction3 = new Transaction();
        transaction3.setType(TransactionType.RECEITA);
        transaction3.setAmount(new BigDecimal("300"));
        transaction3.setDate(LocalDate.of(2024, 11, 10));

        when(transactionService.getUserTransactions(user)).thenReturn(Arrays.asList(transaction1, transaction2, transaction3));

        JFreeChart chart = chartService.createCashFlowChart(user);

        assertNotNull(chart);
        assertEquals("Fluxo de Caixa", chart.getTitle().getText());
        verify(transactionService, times(1)).getUserTransactions(user);
    }
}
