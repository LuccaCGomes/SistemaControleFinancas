package com.trabalho.controlefinancas.controller;

import com.trabalho.controlefinancas.config.GlobalCurrencyConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class CurrencyConfigController {

    @Autowired
    private GlobalCurrencyConfig globalCurrencyConfig;

    @PostMapping("/set-default-currency")
    public String setDefaultCurrency(@RequestParam String globalCurrency, Model model) {
        globalCurrencyConfig.setDefaultCurrency(globalCurrency);
        model.addAttribute("message", "Moeda padrão atualizada para " + globalCurrency);
        return "redirect:/transactions";
    }
}