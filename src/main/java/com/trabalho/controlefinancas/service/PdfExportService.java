package com.trabalho.controlefinancas.service;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

@Service
public class PdfExportService {

    public byte[] generatePdf(String htmlContent) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    public String prepareHtmlWithChart(String htmlTemplate, String chartPieBase64, String chartCashFlowBase64) {
        // Substituir o marcador ${graficoBase64} pelo gráfico gerado
        return htmlTemplate.replace("${graficoPieBase64}", chartPieBase64)
                .replace("${graficoCashFlowBase64}", chartCashFlowBase64);
    }
}
