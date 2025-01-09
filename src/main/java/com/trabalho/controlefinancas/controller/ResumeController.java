package com.trabalho.controlefinancas.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.trabalho.controlefinancas.model.FinancialGoal;
import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.service.FinancialGoalService;
import com.trabalho.controlefinancas.service.TransactionService;

@Controller
public class ResumeController {

    private final TransactionService transactionService;
    private final FinancialGoalService financialGoalService;

    // Constructor injection
    public ResumeController(TransactionService transactionService, FinancialGoalService financialGoalService) {
        this.transactionService = transactionService;
        this.financialGoalService = financialGoalService;
    }

    @GetMapping("/monitoring")
    public String getResumoFinanceiro(@AuthenticationPrincipal User user, Model model) {
        LocalDate currentDate = LocalDate.now();
        int month = currentDate.getMonthValue();
        int year = currentDate.getYear();

        Map<String, BigDecimal> summary = transactionService.getMonthlyFinancialSummary(user, month, year);
        BigDecimal finalBalance = summary.get("finalBalance");

        // Verificar metas com base no saldo final
        financialGoalService.checkGoals(user, finalBalance);

        List<FinancialGoal> goals = financialGoalService.getUserGoals(user);

        model.addAttribute("initialBalance", summary.get("initialBalance"));
        model.addAttribute("totalIncome", summary.get("totalIncome"));
        model.addAttribute("totalExpense", summary.get("totalExpense"));
        model.addAttribute("finalBalance", finalBalance);
        model.addAttribute("goals", goals);

        return "monitoring";
    }

    @PostMapping("/add-goal")
    public String addGoal(
            @RequestParam String description,
            @RequestParam BigDecimal targetAmount,
            @AuthenticationPrincipal User user) {

        FinancialGoal goal = new FinancialGoal();
        goal.setDescription(description);
        goal.setTargetAmount(targetAmount);
        goal.setAchieved(false);
        goal.setUser(user);
        financialGoalService.addFinancialGoal(goal);

        return "redirect:/monitoring";
    }
}
