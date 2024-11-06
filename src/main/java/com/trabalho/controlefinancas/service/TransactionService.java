package com.trabalho.controlefinancas.service;

import com.trabalho.controlefinancas.model.Transaction;
import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> getUserTransactions(User user) {
        return transactionRepository.findByUser(user);
    }

    public Transaction addTransaction(Transaction transaction) {
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