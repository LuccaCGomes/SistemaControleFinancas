package com.trabalho.controlefinancas.controller;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.service.CsvService;

@Controller
public class ImportController {

    private final CsvService csvService;

    // Constructor injection
    public ImportController(CsvService csvService) {
        this.csvService = csvService;
    }

    @PostMapping("/import-csv")
    public String importCSV(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, @AuthenticationPrincipal User user) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Por favor, envie um arquivo válido.");
            return "redirect:/categories"; // Redirecione para a página adequada
        }

        try {
            // Processar o arquivo CSV
            csvService.processCSV(file, user);

            redirectAttributes.addFlashAttribute("message", "Arquivo CSV importado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao importar o arquivo: " + e.getMessage());
        }

        return "redirect:/transactions";
    }
}
