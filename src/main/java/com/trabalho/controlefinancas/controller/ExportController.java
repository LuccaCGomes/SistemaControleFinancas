package com.trabalho.controlefinancas.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.trabalho.controlefinancas.model.Category;
import com.trabalho.controlefinancas.model.Transaction;
import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.repository.CategoryRepository;
import com.trabalho.controlefinancas.service.ChartService;
import com.trabalho.controlefinancas.service.CsvService;
import com.trabalho.controlefinancas.service.PdfExportService;
import com.trabalho.controlefinancas.service.ThymeleafTemplateService;
import com.trabalho.controlefinancas.service.TransactionService;

@Controller
public class ExportController {

    private final PdfExportService pdfExportService;
    private final ChartService chartService;
    private final ThymeleafTemplateService templateService;
    private final CategoryRepository categoryRepository;
    private final TransactionService transactionService;
    private final CsvService csvService;

    // Constructor injection
    public ExportController(PdfExportService pdfExportService,
                            ChartService chartService,
                            ThymeleafTemplateService templateService,
                            CategoryRepository categoryRepository,
                            TransactionService transactionService,
                            CsvService csvService) {
        this.pdfExportService = pdfExportService;
        this.chartService = chartService;
        this.templateService = templateService;
        this.categoryRepository = categoryRepository;
        this.transactionService = transactionService;
        this.csvService = csvService;
    }

    @GetMapping("/export-pdf")
    public ResponseEntity<byte[]> exportPageToPdf(Model model, @AuthenticationPrincipal User user) throws IOException {
        LocalDate currentDate = LocalDate.now();
        int month = currentDate.getMonthValue();
        int year = currentDate.getYear();

        List<Transaction> transactions = transactionService.findByMonth(year, month, user);

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
    public ResponseEntity<byte[]> exportDataToCsv(Model model, @AuthenticationPrincipal User user) {

        List<Transaction> transactions = transactionService.getUserTransactions(user);

        List<String[]> data = new ArrayList<>();
        data.add(new String[]{"ID", "Tipo", "Descrição", "Valor", "Moeda","Data", "Categoria", "É Recorrente?"});

        // Converter cada transação em uma linha CSV
        for (Transaction transaction : transactions) {
            data.add(new String[]{
                transaction.getId().toString(),
                transaction.getType().toString(),
                transaction.getDescription(),
                String.valueOf(transaction.getAmount()),
                transaction.getCurrency().toString(),
                transaction.getDate().toString(),
                transaction.getCategory().getName(),
                transaction.isRecurringString()
            });
        }
        byte[] csvBytes = csvService.generateCsv(data);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=transacoes.csv")
                .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
                .body(csvBytes);
    }
}
