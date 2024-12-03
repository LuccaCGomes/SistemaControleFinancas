package com.trabalho.controlefinancas.controller;

import com.trabalho.controlefinancas.model.Category;
import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.repository.CategoryRepository;
import com.trabalho.controlefinancas.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/add-category")
    public String showAddCategoryForm() {
        return "add-category";
    }


    @GetMapping("/categories")
    public String showCategories(Model model, @AuthenticationPrincipal User user) {
        if (user == null) {
            // Caso o usuário não esteja logado, redireciona para a página de login
            return "redirect:/login";
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
            return "redirect:/login";  // Direciona para página de login
        }
        try {
            Category category = new Category(name, description, budget);
            category.setUser(user);
            categoryService.addCategory(category);
            redirectAttributes.addFlashAttribute("message", "Category successfully added!");
            return "redirect:/categories";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding category. The name might already exist.");
            return "redirect:/add-category";
        }
    }

    @PostMapping("/delete-category/{id}")
    public String deleteCategory(@PathVariable Long id, Authentication authentication) {
        System.out.println("Usuário autenticado: " + authentication.getName());
        categoryService.deleteCategoryById(id);
        return "redirect:/categories";
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
        return "redirect:/categories";
    }
}