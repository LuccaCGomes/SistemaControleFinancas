package com.trabalho.controlefinancas;

import com.trabalho.controlefinancas.service.PdfExportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PdfExportServiceTest {

    @InjectMocks
    private PdfExportService pdfExportService;

    @Test
    void generatePdf_ValidHtml_ReturnsPdfBytes() {
        String validHtml = "<html><body><h1>Test</h1></body></html>";

        byte[] result = pdfExportService.generatePdf(validHtml);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void generatePdf_InvalidHtml_ThrowsException() {
        String invalidHtml = "<<<invalid>>>";

        assertThrows(RuntimeException.class, () -> pdfExportService.generatePdf(invalidHtml));
    }

    @Test
    void prepareHtmlWithChart_ValidInput_ReturnsReplacedHtml() {
        String template = "Template with ${graficoPieBase64} and ${graficoCashFlowBase64}";
        String chartPie = "pie-base64-data";
        String chartCashFlow = "cash-flow-base64-data";

        String result = pdfExportService.prepareHtmlWithChart(template, chartPie, chartCashFlow);

        assertTrue(result.contains("pie-base64-data"));
        assertTrue(result.contains("cash-flow-base64-data"));
        assertFalse(result.contains("${graficoPieBase64}"));
        assertFalse(result.contains("${graficoCashFlowBase64}"));
    }
}