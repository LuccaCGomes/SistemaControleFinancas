package com.trabalho.controlefinancas.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import com.trabalho.controlefinancas.model.Category;
import com.trabalho.controlefinancas.model.Transaction;
import com.trabalho.controlefinancas.model.TransactionType;
import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.repository.CategoryRepository;
import com.trabalho.controlefinancas.service.*;

@ExtendWith(MockitoExtension.class)
class ExportControllerTest {

    @Mock
    private PdfExportService pdfExportService;

    @Mock
    private ChartService chartService;

    @Mock
    private ThymeleafTemplateService templateService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private CsvService csvService;

    @Mock
    private Model model;

    @InjectMocks
    private ExportController exportController;

    private User testUser;
    private List<Transaction> testTransactions;
    private List<Category> testCategories;
    private Map<String, BigDecimal> testSummary;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);

        testTransactions = new ArrayList<>();
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(new BigDecimal("100"));
        transaction.setType(TransactionType.DESPESA);
        transaction.setDate(LocalDate.now());
        testTransactions.add(transaction);

        testCategories = new ArrayList<>();
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        testCategories.add(category);

        testSummary = new HashMap<>();
        testSummary.put("initialBalance", new BigDecimal("1000"));
        testSummary.put("totalIncome", new BigDecimal("500"));
        testSummary.put("totalExpense", new BigDecimal("300"));
        testSummary.put("finalBalance", new BigDecimal("1200"));
    }

    @Test
    void exportPageToPdf_Success() throws Exception {
        // Arrange
        LocalDate currentDate = LocalDate.now();
        when(transactionService.findByMonth(currentDate.getYear(), currentDate.getMonthValue(), testUser))
                .thenReturn(testTransactions);
        when(transactionService.getMonthlyFinancialSummary(testUser, currentDate.getMonthValue(), currentDate.getYear()))
                .thenReturn(testSummary);
        when(categoryRepository.findByUser(testUser)).thenReturn(testCategories);
        when(templateService.renderTemplate(eq("report"), any())).thenReturn("<html>test</html>");
        when(chartService.createExpensePieChartBase64(testUser)).thenReturn("pie-chart-data");
        when(chartService.createCashFlowChartBase64(testUser)).thenReturn("cash-flow-data");
        when(pdfExportService.prepareHtmlWithChart(anyString(), anyString(), anyString()))
                .thenReturn("<html>test with charts</html>");
        when(pdfExportService.generatePdf(anyString())).thenReturn("pdf-content".getBytes());

        // Act
        ResponseEntity<byte[]> response = exportController.exportPageToPdf(model, testUser);

        // Assert
        assertNotNull(response);
        assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
        assertEquals("attachment; filename=relatorio.pdf",
                response.getHeaders().getFirst("Content-Disposition"));
        assertArrayEquals("pdf-content".getBytes(), response.getBody());

        verify(transactionService).findByMonth(currentDate.getYear(), currentDate.getMonthValue(), testUser);
        verify(categoryRepository).findByUser(testUser);
        verify(templateService).renderTemplate(eq("report"), any());
        verify(chartService).createExpensePieChartBase64(testUser);
        verify(chartService).createCashFlowChartBase64(testUser);
        verify(pdfExportService).prepareHtmlWithChart(anyString(), anyString(), anyString());
        verify(pdfExportService).generatePdf(anyString());
    }


    @Test
    void exportPageToPdf_HandlesIOException() throws Exception {
        // Arrange
        LocalDate currentDate = LocalDate.now();
        when(transactionService.findByMonth(currentDate.getYear(), currentDate.getMonthValue(), testUser))
                .thenReturn(testTransactions);
        when(transactionService.getMonthlyFinancialSummary(testUser, currentDate.getMonthValue(), currentDate.getYear()))
                .thenReturn(testSummary);
        when(categoryRepository.findByUser(testUser)).thenReturn(testCategories);

        // Substituindo a exceção verificada por uma RuntimeException para simular o erro.
        when(templateService.renderTemplate(eq("report"), any()))
                .thenThrow(new RuntimeException("Test exception"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> exportController.exportPageToPdf(model, testUser));

        verify(transactionService).findByMonth(currentDate.getYear(), currentDate.getMonthValue(), testUser);
        verify(categoryRepository).findByUser(testUser);
        verify(templateService).renderTemplate(eq("report"), any());
    }

}