package com.trabalho.controlefinancas.controller;
import com.trabalho.controlefinancas.model.Category;
import com.trabalho.controlefinancas.model.Transaction;
import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.repository.CategoryRepository;
import com.trabalho.controlefinancas.service.CsvExportService;
import com.trabalho.controlefinancas.service.PdfExportService;
import com.trabalho.controlefinancas.service.ThymeleafTemplateService;
import com.trabalho.controlefinancas.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ExportController {
    @Autowired
    private PdfExportService pdfExportService;

    @Autowired
    ThymeleafTemplateService templateService;
    @Autowired
    private CategoryRepository categoryRepository;


    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CsvExportService csvExportService;


    @GetMapping("/export-pdf")
    public ResponseEntity<byte[]> exportPageToPdf(Model model, @AuthenticationPrincipal User user) {
        List<Transaction> transactions = transactionService.getUserTransactions(user);

        BigDecimal totalValue = transactions.stream()
                .map(Transaction::getAmount) // Assuming `getAmount()` returns BigDecimal
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        List<Category> categories = categoryRepository.findByUser(user);

        Map<String, Object> variables = new HashMap<>();
        variables.put("transactions", transactions);
        variables.put("categories", categories);


        String htmlContent = templateService.renderTemplate("report", variables);

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
