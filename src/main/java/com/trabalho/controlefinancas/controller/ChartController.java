package com.trabalho.controlefinancas.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;

import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.service.ChartService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChartController {

    private final ChartService chartService;

    // Constructor injection
    public ChartController(ChartService chartService) {
        this.chartService = chartService;
    }

    @GetMapping(value = "/simple-chart", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getExpenseChart(@AuthenticationPrincipal User user) throws IOException {

        JFreeChart chart = chartService.createExpensePieChart(user);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(baos, chart, 600, 400);

        return baos.toByteArray();
    }

    @GetMapping(value = "/cash-flow-chart", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getCashFlowChart(@AuthenticationPrincipal User user) throws IOException {
        JFreeChart chart = chartService.createCashFlowChart(user);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(baos, chart, 600, 400);

        return baos.toByteArray();
    }
}
