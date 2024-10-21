package com.trabalho.controlefinancas.repository;

import com.trabalho.controlefinancas.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
