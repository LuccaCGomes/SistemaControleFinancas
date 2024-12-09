package com.trabalho.controlefinancas.service;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
public class ThymeleafTemplateService {

    private final TemplateEngine templateEngine;

    public ThymeleafTemplateService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String renderTemplate(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        return templateEngine.process(templateName, context);
    }
}
