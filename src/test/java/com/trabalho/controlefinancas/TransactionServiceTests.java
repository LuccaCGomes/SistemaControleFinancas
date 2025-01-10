package com.trabalho.controlefinancas;

import com.trabalho.controlefinancas.model.Transaction;
import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.repository.TransactionRepository;
import com.trabalho.controlefinancas.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void getUserTransactions_ValidUser_ReturnsTransactions() {
        User user = new User();
        List<Transaction> transactions = List.of(new Transaction(), new Transaction());

        when(transactionRepository.findByUser(user)).thenReturn(transactions);

        List<Transaction> result = transactionService.getUserTransactions(user);

        assertEquals(2, result.size());
        verify(transactionRepository, times(1)).findByUser(user);
    }
}
