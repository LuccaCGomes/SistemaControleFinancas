package com.trabalho.controlefinancas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trabalho.controlefinancas.model.FinancialGoal;
import com.trabalho.controlefinancas.model.User;

@Repository
public interface FinancialGoalRepository extends JpaRepository<FinancialGoal, Long> {
    List<FinancialGoal> findByUser(User user);
}