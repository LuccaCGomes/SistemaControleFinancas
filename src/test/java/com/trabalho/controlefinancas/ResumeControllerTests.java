package com.trabalho.controlefinancas.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import com.trabalho.controlefinancas.model.FinancialGoal;
import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.service.FinancialGoalService;
import com.trabalho.controlefinancas.service.TransactionService;

@ExtendWith(MockitoExtension.class)
class ResumeControllerTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private FinancialGoalService financialGoalService;

    @Mock
    private Model model;

    @InjectMocks
    private ResumeController resumeController;

    private User testUser;
    private Map<String, BigDecimal> testSummary;
    private List<FinancialGoal> testGoals;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);

        testSummary = new HashMap<>();
        testSummary.put("initialBalance", new BigDecimal("1000"));
        testSummary.put("totalIncome", new BigDecimal("500"));
        testSummary.put("totalExpense", new BigDecimal("300"));
        testSummary.put("finalBalance", new BigDecimal("1200"));

        testGoals = new ArrayList<>();
        FinancialGoal goal = new FinancialGoal();
        goal.setId(1L);
        goal.setDescription("Test Goal");
        goal.setTargetAmount(new BigDecimal("1000"));
        testGoals.add(goal);
    }

    @Test
    void getResumoFinanceiro_Success() {
        // Arrange
        LocalDate currentDate = LocalDate.now();
        when(transactionService.getMonthlyFinancialSummary(
                eq(testUser),
                eq(currentDate.getMonthValue()),
                eq(currentDate.getYear())
        )).thenReturn(testSummary);
        when(financialGoalService.getUserGoals(testUser)).thenReturn(testGoals);

        // Act
        String viewName = resumeController.getResumoFinanceiro(testUser, model);

        // Assert
        assertEquals("monitoring", viewName);
        verify(model).addAttribute("initialBalance", testSummary.get("initialBalance"));
        verify(model).addAttribute("totalIncome", testSummary.get("totalIncome"));
        verify(model).addAttribute("totalExpense", testSummary.get("totalExpense"));
        verify(model).addAttribute("finalBalance", testSummary.get("finalBalance"));
        verify(model).addAttribute("goals", testGoals);
        verify(model).addAttribute("user", testUser);
        verify(financialGoalService).checkGoals(testUser, testSummary.get("finalBalance"));
        verify(financialGoalService).getUserGoals(testUser);
    }


}