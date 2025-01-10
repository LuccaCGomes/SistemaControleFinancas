package com.trabalho.controlefinancas;

import com.trabalho.controlefinancas.model.FinancialGoal;
import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.repository.FinancialGoalRepository;
import com.trabalho.controlefinancas.service.FinancialGoalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinancialGoalServiceTest {

    @Mock
    private FinancialGoalRepository financialGoalRepository;

    @InjectMocks
    private FinancialGoalService financialGoalService;

    @Test
    void addFinancialGoal_ValidGoal_SavesGoal() {
        FinancialGoal goal = new FinancialGoal();

        financialGoalService.addFinancialGoal(goal);

        verify(financialGoalRepository, times(1)).save(goal);
    }

    @Test
    void getUserGoals_ValidUser_ReturnsGoals() {
        User user = new User();
        List<FinancialGoal> expectedGoals = List.of(new FinancialGoal(), new FinancialGoal());

        when(financialGoalRepository.findByUser(user)).thenReturn(expectedGoals);

        List<FinancialGoal> result = financialGoalService.getUserGoals(user);

        assertEquals(expectedGoals, result);
        verify(financialGoalRepository, times(1)).findByUser(user);
    }

    @Test
    void checkGoals_NoGoalsAchieved_NoGoalsUpdated() {
        User user = new User();
        BigDecimal currentBalance = new BigDecimal("100");

        FinancialGoal goal1 = new FinancialGoal();
        goal1.setTargetAmount(new BigDecimal("200"));
        goal1.setAchieved(false);

        List<FinancialGoal> goals = List.of(goal1);

        when(financialGoalRepository.findByUser(user)).thenReturn(goals);

        financialGoalService.checkGoals(user, currentBalance);

        verify(financialGoalRepository, never()).save(any());
    }

    @Test
    void checkGoals_GoalAchieved_UpdatesGoal() {
        User user = new User();
        BigDecimal currentBalance = new BigDecimal("200");

        FinancialGoal goal1 = new FinancialGoal();
        goal1.setTargetAmount(new BigDecimal("100"));
        goal1.setAchieved(false);

        List<FinancialGoal> goals = List.of(goal1);

        when(financialGoalRepository.findByUser(user)).thenReturn(goals);

        financialGoalService.checkGoals(user, currentBalance);

        assertTrue(goal1.isAchieved());
        verify(financialGoalRepository, times(1)).save(goal1);
    }

    @Test
    void checkGoals_AlreadyAchievedGoal_NoUpdate() {
        User user = new User();
        BigDecimal currentBalance = new BigDecimal("200");

        FinancialGoal goal1 = new FinancialGoal();
        goal1.setTargetAmount(new BigDecimal("100"));
        goal1.setAchieved(true);

        List<FinancialGoal> goals = List.of(goal1);

        when(financialGoalRepository.findByUser(user)).thenReturn(goals);

        financialGoalService.checkGoals(user, currentBalance);

        verify(financialGoalRepository, never()).save(any());
    }

    @Test
    void checkGoals_MultipleGoals_UpdatesOnlyAchieved() {
        User user = new User();
        BigDecimal currentBalance = new BigDecimal("150");

        FinancialGoal goal1 = new FinancialGoal();
        goal1.setTargetAmount(new BigDecimal("100"));
        goal1.setAchieved(false);

        FinancialGoal goal2 = new FinancialGoal();
        goal2.setTargetAmount(new BigDecimal("200"));
        goal2.setAchieved(false);

        List<FinancialGoal> goals = List.of(goal1, goal2);

        when(financialGoalRepository.findByUser(user)).thenReturn(goals);

        financialGoalService.checkGoals(user, currentBalance);

        assertTrue(goal1.isAchieved());
        assertFalse(goal2.isAchieved());
        verify(financialGoalRepository, times(1)).save(goal1);
        verify(financialGoalRepository, never()).save(goal2);
    }
}