package com.trabalho.controlefinancas.controller;

import com.trabalho.controlefinancas.model.Transaction;
import com.trabalho.controlefinancas.model.TransactionType;
import com.trabalho.controlefinancas.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/transactions")
    public String showTransactions(Model model) {
        model.addAttribute("transactions", transactionService.getAllTransactions());
        return "transactions";
    }

    @GetMapping("/add-transaction")
    public String showAddTransactionForm() {
        return "add-transaction";
    }

    @PostMapping("/add-transaction")
    public String addTransaction(
            @RequestParam String type,
            @RequestParam String category,
            @RequestParam BigDecimal amount,
            @RequestParam String date,
            @RequestParam String description,
            Model model) {

        Transaction transaction = new Transaction(
                TransactionType.valueOf(type.toUpperCase()),
                category,
                amount,
                LocalDate.parse(date),
                description
        );

        transactionService.addTransaction(transaction);
        model.addAttribute("message", "Transação adicionada com sucesso!");
        return "redirect:/transactions";
    }
}
