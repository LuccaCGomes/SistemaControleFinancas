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
import java.time.LocalDate;
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

    public String addTransaction(Transaction transaction) {
        Category category = transaction.getCategory();
        User user = transaction.getUser();

        // Obtém o mês e o ano da transação
        LocalDate transactionDate = transaction.getDate();
        int month = transactionDate.getMonthValue();
        int year = transactionDate.getYear();

        BigDecimal totalTransactionsAmount = transactionRepository
                .findByCategoryAndUser(category, user)
                .stream()
                .filter(t -> t.getDate().getMonthValue() == month && t.getDate().getYear() == year) // Verifica o mesmo mês e ano
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal newTotal = totalTransactionsAmount.add(transaction.getAmount());

        // Salva a transação mesmo se exceder o limite do mês
        transactionRepository.save(transaction);

        if (newTotal.compareTo(category.getBudget()) > 0) {

            return "O valor das transações para a categoria " + category.getName() + " excedeu o orçamento mensal.";
        }

        return null;
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