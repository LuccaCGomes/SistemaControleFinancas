package com.trabalho.controlefinancas.initializer;

import com.trabalho.controlefinancas.model.Category;
import com.trabalho.controlefinancas.repository.CategoryRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {
    private final CategoryRepository repository;

    public DataInitializer(CategoryRepository repository) {
        this.repository = repository;
    }
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (repository.count() == 0) { // Check if data already exists
            repository.save(new Category("Alimentação", "Gastos com Alimentos")); // Add your default data
            repository.save(new Category("Transporte", "Gastos com Transporte")); // Add your default data
            repository.save(new Category("Saúde", "Gastos com Saúde")); // Add your default data
            repository.save(new Category("Salário", "Recebimento do Salário")); // Add your default data

            // add more as needed
        }
    }
}


