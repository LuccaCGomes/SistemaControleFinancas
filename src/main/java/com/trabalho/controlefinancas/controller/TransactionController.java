package com.trabalho.controlefinancas.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.trabalho.controlefinancas.exception.BudgetExceededException;
import com.trabalho.controlefinancas.model.Category;
import com.trabalho.controlefinancas.model.Transaction;
import com.trabalho.controlefinancas.model.TransactionType;
import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.repository.CategoryRepository;
import com.trabalho.controlefinancas.service.TransactionService;

@Controller
public class TransactionController {

    private final TransactionService transactionService;
    private final CategoryRepository categoryRepository;

    // Constructor injection
    public TransactionController(TransactionService transactionService, CategoryRepository categoryRepository) {
        this.transactionService = transactionService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/transactions")
    public String showTransactions(Model model, @AuthenticationPrincipal User user) {
        List<Transaction> transactions = transactionService.getUserTransactions(user);

        BigDecimal totalValue = transactions.stream()
                .map(Transaction::getAmount) // Assuming `getAmount()` returns BigDecimal
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        List<Category> categories = categoryRepository.findByUser(user);
        model.addAttribute("categories", categories);
        model.addAttribute("totalValue", totalValue);
        model.addAttribute("transactions", transactions);
        return "transactions";
    }

    @GetMapping("/add-transaction")
    public String showAddTransactionForm(Model model ,@AuthenticationPrincipal User user) {
        List<Category> categories = categoryRepository.findByUser(user);
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
            @AuthenticationPrincipal User user,
            RedirectAttributes redirectAttributes
            ) {

        Transaction transaction = new Transaction(
                TransactionType.valueOf(type.toUpperCase()),
                category,
                amount,
                LocalDate.parse(date),
                description,
                isRecurring,
                user
        );

        try {
            String errorMessage = transactionService.addTransaction(transaction);
            if (errorMessage != null) {
                redirectAttributes.addFlashAttribute("error", errorMessage);
                return "redirect:/transactions";
            } else {
                redirectAttributes.addFlashAttribute("message", "Transação adicionada com sucesso!");
                return "redirect:/transactions";
            }
        } catch (BudgetExceededException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/transactions";
        }
    }

    @PostMapping("/delete-transaction/{id}")
    public String deleteTransaction(@PathVariable Long id, @AuthenticationPrincipal User user) {
        transactionService.deleteTransactionByIdAndUser(id, user);
        return "redirect:/transactions";
    }

    @PostMapping("/edit-transaction")
    public String editTransaction(
            @RequestParam String type,
            @RequestParam Long id,
            @RequestParam Long category,
            @RequestParam String date,
            @RequestParam String description,
            @RequestParam BigDecimal amount,
            RedirectAttributes redirectAttributes) {

        Transaction transaction = transactionService.findById(id);
        transaction.setType(TransactionType.valueOf(type.toUpperCase()));
        transaction.setDate(LocalDate.parse(date));
        transaction.setDescription(description);
        transaction.setCategory(categoryRepository.findById(category).orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada")));
        transaction.setAmount(amount);

        transactionService.updateTransaction(transaction);

        redirectAttributes.addFlashAttribute("message", "Transação editada com sucesso!");
        return "redirect:/transactions";
    }
}