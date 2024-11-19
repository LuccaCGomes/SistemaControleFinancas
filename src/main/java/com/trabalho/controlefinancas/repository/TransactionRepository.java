package com.trabalho.controlefinancas.repository;

import com.trabalho.controlefinancas.model.Category;
import com.trabalho.controlefinancas.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.trabalho.controlefinancas.model.User;

import java.util.List;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    //Find Transactions by User
    List<Transaction> findByUser(User user);

    List<Transaction> findByCategoryAndUser(Category category, User user);
}
