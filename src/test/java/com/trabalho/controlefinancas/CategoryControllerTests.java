package com.trabalho.controlefinancas;

import com.trabalho.controlefinancas.controller.CategoryController;
import com.trabalho.controlefinancas.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
        // Este teste irá verificar se o contexto do Spring carrega corretamente
    }
    @MockBean
    private CategoryService categoryService;

    @Test
    public void showAddCategoryForm_ShouldReturnAddCategoryView() throws Exception {
        mockMvc.perform(get("/add-category")) // Simula uma requisição GET para /add-category
                .andExpect(view().name("add-category")); // Verifica se o nome da view retornada é "add-category"
    }
}
