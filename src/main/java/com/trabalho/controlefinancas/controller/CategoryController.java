package com.trabalho.controlefinancas.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.trabalho.controlefinancas.model.Category;
import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.service.CategoryService;

@Controller
public class CategoryController {

    private static final String REDIRECT_CATEGORIES = "redirect:/categories";
    private static final String REDIRECT_LOGIN = "redirect:/login";
    private static final String REDIRECT_ADD_CATEGORY = "redirect:/add-category";


    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/add-category")
    public String showAddCategoryForm() {
        return "add-category";
    }

    @GetMapping("/categories")
    public String showCategories(Model model, @AuthenticationPrincipal User user) {
        if (user == null) {
            // Caso o usuário não esteja logado, redireciona para a página de login
            return REDIRECT_LOGIN;
        }
        List<Category> categories = categoryService.getAllCategoriesByUser(user);
        model.addAttribute("categories", categories);
        return "categories";  // Nome da view que será renderizada
    }

    @PostMapping("/add-category")
    public String addCategory(@RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) BigDecimal budget,
            @AuthenticationPrincipal User user,
            RedirectAttributes redirectAttributes) {
        if (user == null) {
            // Se o usuário não estiver autenticado, redireciona para a página de login
            redirectAttributes.addFlashAttribute("error", "User is not authenticated.");
            return REDIRECT_LOGIN;  // Direciona para página de login
        }
        try {
            Category category = new Category(name, description, budget);
            category.setUser(user);
            categoryService.addCategory(category);
            redirectAttributes.addFlashAttribute("message", "Category successfully added!");
            return REDIRECT_CATEGORIES;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding category. The name might already exist.");
            return REDIRECT_ADD_CATEGORY;
        }
    }

    @PostMapping("/delete-category/{id}")
    public String deleteCategory(@PathVariable Long id, Authentication authentication) {
        categoryService.deleteCategoryById(id);
        return REDIRECT_CATEGORIES;
    }

    @PostMapping("/edit-category")
    public String editCategory(
            @RequestParam Long id,
            @RequestParam String name,
            @RequestParam(required = false) BigDecimal budget,
            @RequestParam(required = false) String description,
            RedirectAttributes redirectAttributes) {
        Category category = categoryService.findById(id);
        category.setName(name);
        category.setBudget(budget);
        category.setDescription(description);
        categoryService.updateCategory(category);

        redirectAttributes.addFlashAttribute("message", "Categoria editada com sucesso!");
        return REDIRECT_CATEGORIES;
    }
}
