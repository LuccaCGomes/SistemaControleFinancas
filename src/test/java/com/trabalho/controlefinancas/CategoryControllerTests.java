package com.trabalho.controlefinancas.controller;

import com.trabalho.controlefinancas.model.Category;
import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void showAddCategoryForm_ReturnsViewName() {
        String result = categoryController.showAddCategoryForm();
        assertEquals("add-category", result);
    }

    @Test
    void showCategories_UserNotAuthenticated_RedirectsToLogin() {
        String result = categoryController.showCategories(model, null);
        assertEquals("redirect:/login", result);
    }

    @Test
    void showCategories_UserAuthenticated_ReturnsCategoriesView() {
        User user = new User();
        List<Category> categories = List.of(new Category("Food", "Description", BigDecimal.TEN));

        when(categoryService.getAllCategoriesByUser(user)).thenReturn(categories);

        String result = categoryController.showCategories(model, user);

        assertEquals("categories", result);
        verify(model, times(1)).addAttribute("categories", categories);
    }

    @Test
    void addCategory_UserNotAuthenticated_RedirectsToLogin() {
        String result = categoryController.addCategory("Food", "Description", BigDecimal.TEN, null, redirectAttributes);

        assertEquals("redirect:/login", result);
        verify(redirectAttributes, times(1)).addFlashAttribute("error", "User is not authenticated.");
    }

    @Test
    void addCategory_ValidCategory_AddsCategoryAndRedirects() {
        User user = new User();
        Category category = new Category("Food", "Description", BigDecimal.TEN);
        category.setUser(user);

        String result = categoryController.addCategory("Food", "Description", BigDecimal.TEN, user, redirectAttributes);

        assertEquals("redirect:/categories", result);
        verify(categoryService, times(1)).addCategory(any(Category.class));
        verify(redirectAttributes, times(1)).addFlashAttribute("message", "Category successfully added!");
    }

    @Test
    void addCategory_DuplicateCategory_ReturnsError() {
        User user = new User();

        doThrow(new RuntimeException("Duplicate"))
                .when(categoryService).addCategory(any(Category.class));

        String result = categoryController.addCategory("Food", "Description", BigDecimal.TEN, user, redirectAttributes);

        assertEquals("redirect:/add-category", result);
        verify(redirectAttributes, times(1)).addFlashAttribute("error", "Error adding category. The name might already exist.");
    }


}
