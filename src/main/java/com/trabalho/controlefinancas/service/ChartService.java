package com.trabalho.controlefinancas.service;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import com.trabalho.controlefinancas.model.Transaction;
import com.trabalho.controlefinancas.model.TransactionType;
import com.trabalho.controlefinancas.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.math.BigDecimal;
import java.util.*;
import java.time.ZoneId;


import org.jfree.chart.ChartUtils;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

@Service
public class ChartService {

    @Autowired
    private TransactionService transactionService;

    public JFreeChart createExpensePieChart(User user) {
        List<Transaction> transactions = transactionService.getUserTransactions(user);

        // Utiliza um Map temporário para acumular os valores das categorias
        Map<String, Double> tempCategoryAmounts = new HashMap<>();

        transactions.stream()
                .filter(transaction -> transaction.getType() == TransactionType.DESPESA)
                .forEach(transaction -> {
                    String categoryName = transaction.getCategory().getName();
                    BigDecimal amount = transaction.getAmount();

                    // Se já existe um valor para a categoria, soma o valor da transação
                    double currentAmount = tempCategoryAmounts.getOrDefault(categoryName, 0.0);
                    tempCategoryAmounts.put(categoryName, currentAmount + amount.doubleValue());
                });

        // Cria o dataset final usando os valores acumulados no Map
        DefaultPieDataset dataset = new DefaultPieDataset();
        tempCategoryAmounts.forEach((category, totalAmount) -> {
            dataset.setValue(category, totalAmount);
        });

        // Cria o gráfico de pizza com os dados do dataset
        JFreeChart chart = ChartFactory.createPieChart(
                "Distribuição de Despesas", // Título
                dataset,                   // Dataset
                true,                       // Legenda visível
                true,                       // Tooltips ativados
                false                       // URLs desativadas
        );

        // Ajusta o gráfico (exemplo: remove contornos das fatias)
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionOutlinesVisible(false);

        return chart; // Retorna o gráfico gerado
    }

    public String createExpensePieChartBase64(User user) throws IOException {
        JFreeChart chart =createExpensePieChart(user);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(baos, chart, 600, 400);
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    public JFreeChart createCashFlowChart(User user) {
        List<Transaction> transactions = transactionService.getUserTransactions(user);

        // Ordenar transações por data
        transactions.sort(Comparator.comparing(Transaction::getDate));

        // Cria a série de dados para o gráfico
        TimeSeries series = new TimeSeries("Saldo Acumulado");
        BigDecimal saldoAcumulado = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            BigDecimal valor = transaction.getType() == TransactionType.RECEITA
                    ? transaction.getAmount()
                    : transaction.getAmount().negate();
            saldoAcumulado = saldoAcumulado.add(valor);

            // Adiciona o saldo acumulado à série
            Date date = Date.from(transaction.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
            series.addOrUpdate(new Day(date), saldoAcumulado.doubleValue());
        }

        // Adiciona a série ao conjunto de dados
        TimeSeriesCollection dataset = new TimeSeriesCollection(series);

        // Configura o gráfico
        XYPlot plot = new XYPlot(
                dataset,
                new DateAxis("Data"),
                new NumberAxis("Saldo Acumulado"),
                new XYLineAndShapeRenderer(true, false)
        );

        return new JFreeChart("Fluxo de Caixa", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
    }

    public String createCashFlowChartBase64(User user) throws IOException {
        JFreeChart chart =createCashFlowChart(user);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(baos, chart, 600, 400);
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
    }
}
