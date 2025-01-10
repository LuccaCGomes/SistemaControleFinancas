package com.trabalho.controlefinancas.config;

import org.springframework.stereotype.Component;

@Component
public class GlobalCurrencyConfig {

    private String defaultCurrency = "BRL";

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }
}