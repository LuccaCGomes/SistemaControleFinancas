package com.trabalho.controlefinancas.service;

import com.trabalho.controlefinancas.exception.BudgetExceededException;
import com.trabalho.controlefinancas.model.Category;
import com.trabalho.controlefinancas.model.Transaction;
import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> getUserTransactions(User user) {
        return transactionRepository.findByUser(user);
    }

//    public Transaction addTransaction(Transaction transaction) {
//        return transactionRepository.save(transaction);
//    }

    public Transaction addTransaction(Transaction transaction) {
        Category category = transaction.getCategory();
        User user = transaction.getUser();

        // Verifica se a soma das transações da categoria do usuário está abaixo do orçamento
        BigDecimal totalTransactionsAmount = transactionRepository
                .findByCategoryAndUser(category, user)
                .stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal newTotal = totalTransactionsAmount.add(transaction.getAmount());

        if (newTotal.compareTo(category.getBudget()) > 0) {
            throw new BudgetExceededException("Adding this transaction exceeds the budget limit for the category.");
        }

        return transactionRepository.save(transaction);
    }

    public void deleteTransactionByIdAndUser(Long id, User user) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Not authorized to delete this transaction");
        }

        transactionRepository.delete(transaction);
    }
}