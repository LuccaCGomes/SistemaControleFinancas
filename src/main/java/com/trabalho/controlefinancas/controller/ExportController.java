package com.trabalho.controlefinancas.controller;
import com.trabalho.controlefinancas.model.Category;
import com.trabalho.controlefinancas.model.Transaction;
import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.repository.CategoryRepository;
import com.trabalho.controlefinancas.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ExportController {
    @Autowired
    private PdfExportService pdfExportService;
    @Autowired
    private ChartService chartService;
    @Autowired
    ThymeleafTemplateService templateService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private CsvExportService csvExportService;


    @GetMapping("/export-pdf")
    public ResponseEntity<byte[]> exportPageToPdf(Model model, @AuthenticationPrincipal User user) throws IOException {
        LocalDate currentDate = LocalDate.now();
        int month = currentDate.getMonthValue();
        int year = currentDate.getYear();

        List<Transaction> transactions = transactionService.findByMonth(year,month,user);

        Map<String, BigDecimal> summary = transactionService.getMonthlyFinancialSummary(user, month, year);

        BigDecimal totalValue = transactions.stream()
                .map(Transaction::getAmount) // Assuming `getAmount()` returns BigDecimal
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        List<Category> categories = categoryRepository.findByUser(user);

        Map<String, Object> variables = new HashMap<>();
        variables.put("transactions", transactions);
        variables.put("categories", categories);
        variables.put("initialBalance", summary.get("initialBalance"));
        variables.put("totalIncome", summary.get("totalIncome"));
        variables.put("totalExpense", summary.get("totalExpense"));
        variables.put("finalBalance", summary.get("finalBalance"));


        String htmlContent = templateService.renderTemplate("report", variables);
        String pieChart = chartService.createExpensePieChartBase64(user);
        String cashFlowChart = chartService.createCashFlowChartBase64(user);
        htmlContent = pdfExportService.prepareHtmlWithChart(htmlContent, pieChart, cashFlowChart);

        byte[] pdfBytes = pdfExportService.generatePdf(htmlContent);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=relatorio.pdf")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/export-csv")
    public ResponseEntity<byte[]> exportDataToCsv() {
        List<String[]> data = Arrays.asList(
                new String[]{"ID", "Nome", "Valor"},
                new String[]{"1", "Transação 1", "100.00"},
                new String[]{"2", "Transação 2", "200.00"}
        );

        byte[] csvBytes = csvExportService.generateCsv(data);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=transacoes.csv")
                .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
                .body(csvBytes);
    }
}
