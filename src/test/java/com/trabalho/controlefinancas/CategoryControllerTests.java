package com.trabalho.controlefinancas;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.trabalho.controlefinancas.controller.CategoryController;
import com.trabalho.controlefinancas.service.CategoryService;

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
}
