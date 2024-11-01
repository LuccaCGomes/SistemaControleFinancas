package com.trabalho.controlefinancas.controller;

import com.trabalho.controlefinancas.model.Category;
import com.trabalho.controlefinancas.model.Transaction;
import com.trabalho.controlefinancas.model.TransactionType;
import com.trabalho.controlefinancas.service.TransactionService;
import com.trabalho.controlefinancas.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/transactions")
    public String showTransactions(Model model) {
        model.addAttribute("transactions", transactionService.getAllTransactions());
        return "transactions";
    }

    @GetMapping("/add-transaction")
    public String showAddTransactionForm(Model model) {
        // Add this line to make categories available in the form
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        return "add-transaction";
    }

    @PostMapping("/add-transaction")
    public String addTransaction(
            @RequestParam String type,
            @RequestParam Category category,
            @RequestParam BigDecimal amount,
            @RequestParam String date,
            @RequestParam String description,
            @RequestParam(defaultValue = "false") boolean isRecurring,
            Model model) {

        Transaction transaction = new Transaction(
                TransactionType.valueOf(type.toUpperCase()),
                category,
                amount,
                LocalDate.parse(date),
                description,
                isRecurring
        );

        transactionService.addTransaction(transaction);
        model.addAttribute("message", "Transação adicionada com sucesso!");
        return "redirect:/transactions";
    }

    @PostMapping("/delete-transaction/{id}")
    public String deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransactionById(id);
        return "redirect:/transactions";
    }
}
