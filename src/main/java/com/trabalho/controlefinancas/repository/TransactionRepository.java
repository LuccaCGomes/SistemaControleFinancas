package com.trabalho.controlefinancas.repository;

import com.trabalho.controlefinancas.model.Category;
import com.trabalho.controlefinancas.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.trabalho.controlefinancas.model.User;

import java.util.List;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    //Find Transactions by User
    List<Transaction> findByUser(User user);

    List<Transaction> findByCategoryAndUser(Category category, User user);

    @Query("SELECT t FROM Transaction t WHERE YEAR(t.date) = :year AND MONTH(t.date) = :month AND t.user = :user")
    List<Transaction> findTransactionsByMonthAndUser(@Param("year") int year, @Param("month") int month, @Param("user") User user);

}
