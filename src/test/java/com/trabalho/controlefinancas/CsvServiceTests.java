package com.trabalho.controlefinancas.service;

import com.trabalho.controlefinancas.model.Category;
import com.trabalho.controlefinancas.model.Transaction;
import com.trabalho.controlefinancas.model.TransactionType;
import com.trabalho.controlefinancas.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CsvServiceTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private CsvService csvService;

    private User user;
    private Category category;
    private String validCsvContent;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        category = new Category();
        category.setId(1L);
        category.setName("Alimentação");

        validCsvContent = "ID,Tipo,Descrição,Valor,Data,Categoria,É Recorrente?\n" +
                "1,RECEITA,Salário,5000.00,2024-01-15,Alimentação,Sim\n";
    }



    @Test
    void generateCsv_WithEmptyData_ShouldReturnEmptyByteArray() {
        // Arrange
        List<String[]> emptyData = new ArrayList<>();

        // Act
        byte[] result = csvService.generateCsv(emptyData);

        // Assert
        assertNotNull(result);
        assertEquals(0, new String(result, StandardCharsets.UTF_8).trim().length());
    }

    @Test
    void generateCsv_WithNullData_ShouldThrowRuntimeException() {
        // Act & Assert
        assertThrows(RuntimeException.class, () -> csvService.generateCsv(null));
    }

    @Test
    void processCSV_WithValidFile_ShouldProcessSuccessfully() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                validCsvContent.getBytes()
        );

        when(categoryService.findByNameAndUser("Alimentação", user))
                .thenReturn(category);

        // Act
        csvService.processCSV(file, user);

        // Assert
        verify(categoryService).findByNameAndUser("Alimentação", user);
        verify(transactionService).addTransaction(any(Transaction.class));
    }

    @Test
    void processCSV_WithInvalidDate_ShouldThrowException() {
        // Arrange
        String invalidDateContent =
                "ID,Tipo,Descrição,Valor,Data,Categoria,É Recorrente?\n" +
                        "1,RECEITA,Salário,5000.00,2024-13-45,Alimentação,Sim";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                invalidDateContent.getBytes()
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                csvService.processCSV(file, user));
        verify(transactionService, never()).addTransaction(any());
    }

    @Test
    void processCSV_WithEmptyFields_ShouldProcessWithNulls() throws Exception {
        // Arrange
        String contentWithEmptyFields =
                "ID,Tipo,Descrição,Valor,Data,Categoria,É Recorrente?\n" +
                        "1,RECEITA,,,,Alimentação,Não\n";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                contentWithEmptyFields.getBytes()
        );

        when(categoryService.findByNameAndUser("Alimentação", user))
                .thenReturn(category);

        // Act
        csvService.processCSV(file, user);

        // Assert
        verify(transactionService).addTransaction(any(Transaction.class));
    }

    @Test
    void processCSV_WithInvalidAmount_ShouldThrowException() {
        // Arrange
        String invalidAmountContent =
                "ID,Tipo,Descrição,Valor,Data,Categoria,É Recorrente?\n" +
                        "1,RECEITA,Salário,ABC,2024-01-15,Alimentação,Sim";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                invalidAmountContent.getBytes()
        );

        // Act & Assert
        assertThrows(NumberFormatException.class, () ->
                csvService.processCSV(file, user));
        verify(transactionService, never()).addTransaction(any());
    }

    @Test
    void processCSV_WithEmptyFile_ShouldNotProcessAnyTransactions() throws Exception {
        // Arrange
        String onlyHeader = "ID,Tipo,Descrição,Valor,Data,Categoria,É Recorrente?\n";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                onlyHeader.getBytes()
        );

        // Act
        csvService.processCSV(file, user);

        // Assert
        verify(transactionService, never()).addTransaction(any());
    }
}