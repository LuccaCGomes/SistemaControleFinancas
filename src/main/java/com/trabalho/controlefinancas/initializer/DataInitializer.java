//package com.trabalho.controlefinancas.initializer;
//
//import com.trabalho.controlefinancas.model.Category;
//import com.trabalho.controlefinancas.repository.CategoryRepository;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//
//@Component
//public class DataInitializer implements ApplicationRunner {
//    private final CategoryRepository repository;
//
////    public DataInitializer(CategoryRepository repository) {
////        this.repository = repository;
////    }
////    @Override
////    public void run(ApplicationArguments args) throws Exception {
////        if (repository.count() == 0) { // Check if data already exists
////            repository.save(new Category("Alimentação", "Gastos com Alimentos")); // Add your default data
////            repository.save(new Category("Transporte", BigDecimal.valueOf(1000))); // Add your default data
////            repository.save(new Category("Saúde", "Gastos com Saúde", BigDecimal.valueOf(1500))); // Add your default data
////            repository.save(new Category("Salário")); // Add your default data
////
////            // add more as needed
////        }
////    }
//}


