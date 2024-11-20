package com.trabalho.controlefinancas.controller;

import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Controller
public class ResumeController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/monitoring")
    public String getResumoFinanceiro(@AuthenticationPrincipal User user, Model model) {
        LocalDate currentDate = LocalDate.now();
        int month = currentDate.getMonthValue();
        int year = currentDate.getYear();

        Map<String, BigDecimal> summary = transactionService.getMonthlyFinancialSummary(user, month, year);

        model.addAttribute("initialBalance", summary.get("initialBalance"));
        model.addAttribute("totalIncome", summary.get("totalIncome"));
        model.addAttribute("totalExpense", summary.get("totalExpense"));
        model.addAttribute("finalBalance", summary.get("finalBalance"));

        return "monitoring";
    }
}
