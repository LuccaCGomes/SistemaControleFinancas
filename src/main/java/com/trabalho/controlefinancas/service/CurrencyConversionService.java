package com.trabalho.controlefinancas.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
public class CurrencyConversionService {

    // Tabela de taxas de câmbio
    private static final Map<String, BigDecimal> EXCHANGE_RATES = new HashMap<>();

    static {
        // Taxas de exemplo
        EXCHANGE_RATES.put("BRL_TO_USD", BigDecimal.valueOf(0.20)); // 1 BRL = 0.20 USD
        EXCHANGE_RATES.put("BRL_TO_EUR", BigDecimal.valueOf(0.18)); // 1 BRL = 0.18 EUR
        EXCHANGE_RATES.put("USD_TO_BRL", BigDecimal.valueOf(5.00)); // 1 USD = 5.00 BRL
        EXCHANGE_RATES.put("USD_TO_EUR", BigDecimal.valueOf(0.90)); // 1 USD = 0.90 EUR
        EXCHANGE_RATES.put("EUR_TO_BRL", BigDecimal.valueOf(5.50)); // 1 EUR = 5.50 BRL
        EXCHANGE_RATES.put("EUR_TO_USD", BigDecimal.valueOf(1.11)); // 1 EUR = 1.11 USD
    }

    /**
     * Converte um valor de uma moeda para outra usando as taxas locais.
     *
     * @param fromCurrency A moeda de origem (ex.: BRL, USD, EUR).
     * @param toCurrency   A moeda de destino (ex.: BRL, USD, EUR).
     * @param amount       O valor a ser convertido.
     * @return O valor convertido.
     */
    public BigDecimal convert(String fromCurrency, String toCurrency, BigDecimal amount) {
        if (fromCurrency.equals(toCurrency)) {
            return amount; // Sem conversão necessária
        }

        String key = fromCurrency + "_TO_" + toCurrency;
        BigDecimal rate = EXCHANGE_RATES.get(key);

        if (rate == null) {
            throw new IllegalArgumentException("Conversão de moeda não suportada: " + key);
        }

        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
}
