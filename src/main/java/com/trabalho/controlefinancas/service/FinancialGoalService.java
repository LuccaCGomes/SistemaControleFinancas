package com.trabalho.controlefinancas.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.trabalho.controlefinancas.model.FinancialGoal;
import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.repository.FinancialGoalRepository;

@Service
public class FinancialGoalService {

    private final FinancialGoalRepository financialGoalRepository;

    // Construtor para injeção de dependência
    public FinancialGoalService(FinancialGoalRepository financialGoalRepository) {
        this.financialGoalRepository = financialGoalRepository;
    }

    public void addFinancialGoal(FinancialGoal goal) {
        financialGoalRepository.save(goal);
    }

    public List<FinancialGoal> getUserGoals(User user) {
        return financialGoalRepository.findByUser(user);
    }

    public void checkGoals(User user, BigDecimal currentBalance) {
        List<FinancialGoal> goals = financialGoalRepository.findByUser(user);
        for (FinancialGoal goal : goals) {
            if (!goal.isAchieved() && currentBalance.compareTo(goal.getTargetAmount()) >= 0) {
                goal.setAchieved(true);
                financialGoalRepository.save(goal);
            }
        }
    }
}
