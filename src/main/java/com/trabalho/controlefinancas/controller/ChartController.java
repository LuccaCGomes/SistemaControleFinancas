package com.trabalho.controlefinancas.controller;

import com.trabalho.controlefinancas.service.ChartService;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import com.trabalho.controlefinancas.model.User;

@Controller
public class ChartController {

    @Autowired
    private ChartService chartService;

    @GetMapping(value = "/simple-chart", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] getExpenseChart(@AuthenticationPrincipal User user) throws IOException {
        //System.out.println("Iniciando geração do gráfico para o usuário autenticado: " + user.getUsername());

        JFreeChart chart = chartService.createExpensePieChart(user);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(baos, chart, 600, 400);
        //System.out.println("Gráfico gerado e convertido para PNG com sucesso");

        return baos.toByteArray();
    }

    @GetMapping(value = "/cash-flow-chart", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] getCashFlowChart(@AuthenticationPrincipal User user) throws IOException {
        JFreeChart chart = chartService.createCashFlowChart(user);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(baos, chart, 600, 400);

        return baos.toByteArray();
    }
}
