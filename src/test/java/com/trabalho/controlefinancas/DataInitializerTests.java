//package com.trabalho.controlefinancas;
//
//import com.trabalho.controlefinancas.initializer.DataInitializer;
//import com.trabalho.controlefinancas.model.Category;
//import com.trabalho.controlefinancas.repository.CategoryRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.context.annotation.Import;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//@Import(DataInitializer.class) // Importa o DataInitializer para ser testado
//public class DataInitializerTests {
//
//    @Autowired
//    private CategoryRepository repository;
//
//    @Autowired
//    private DataInitializer dataInitializer;
//
//    @BeforeEach
//    public void setup() {
//        repository.deleteAll(); // Limpa o repositório antes de cada teste para garantir consistência
//    }
//
////    @Test
////    public void testRunShouldInitializeDataWhenRepositoryIsEmpty() throws Exception {
////        // Executa o método run para inicializar os dados
////        dataInitializer.run(null);
////
////        // Verifica se as categorias foram salvas corretamente
////        assertThat(repository.count()).isEqualTo(4); // Espera 4 categorias
////
////        // Verifica se uma categoria específica existe
////        assertThat(repository.findByUser("Alimentação")).isNotNull();
////        assertThat(repository.findByUser("Transporte")).isNotNull();
////        assertThat(repository.findByUser("Saúde")).isNotNull();
////        assertThat(repository.findByUser("Salário")).isNotNull();
////    }
//
//    @Test
//    public void testRunShouldNotInitializeDataWhenRepositoryIsNotEmpty() throws Exception {
//        // Configura o repositório com dados iniciais
//        repository.save(new Category("Teste", "Categoria de teste"));
//
//        // Executa o método run novamente
//        dataInitializer.run(null);
//
//        // Verifica que a contagem ainda é 1, pois o DataInitializer não deve adicionar novas categorias
//        assertThat(repository.count()).isEqualTo(1);
//    }
//}