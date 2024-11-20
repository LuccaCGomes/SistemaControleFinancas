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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
