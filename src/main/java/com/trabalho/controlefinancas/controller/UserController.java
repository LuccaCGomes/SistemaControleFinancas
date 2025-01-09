package com.trabalho.controlefinancas.controller;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.trabalho.controlefinancas.model.Category;
import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.service.CategoryService;
import com.trabalho.controlefinancas.service.UserService;

@Controller
public class UserController {

    private final UserService userService;
    private final CategoryService categoryService;

    // Constructor Injection
    public UserController(UserService userService, CategoryService categoryService) {
        this.userService = userService;
        this.categoryService = categoryService;
    }
    @GetMapping("/loggedin")
    public String loggedin() {
        return "loggedin";
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password, Model model, RedirectAttributes redirectAttributes) {
        try {
            userService.registerUser(username, password);
            //Divida Têcnica para inicialiar categoria padrão.
            User user = userService.findUserByUsername(username);
            Category baseCategory1 = new Category("Alimentação", "Gastos com Alimentos");
            baseCategory1.setUser(user);
            categoryService.addCategory(baseCategory1);
            Category baseCategory2 = new Category("Transporte", BigDecimal.valueOf(1000));
            baseCategory2.setUser(user);
            categoryService.addCategory(baseCategory2);
            Category baseCategory3 = new Category("Saúde", "Gastos com Saúde", BigDecimal.valueOf(1500));
            baseCategory3.setUser(user);
            categoryService.addCategory(baseCategory3);
            Category baseCategory4 = new Category("Salário");
            baseCategory4.setUser(user);
            categoryService.addCategory(baseCategory4);
            //termina a dívida tecnica
            redirectAttributes.addFlashAttribute("successMessage", "Cadastro concluído! Faça login.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Nome de usuário já existe. Por favor escolha um outro nome de usuário.");
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String username, @RequestParam String password, Model model) {
        if (userService.loginUser(username, password)) {
            model.addAttribute("username", username);
            model.addAttribute("message", "Usuário logado com sucesso!");
            return "loggedin";
        } else {
            model.addAttribute("errorMessage", "Usuário ou senha inválidos");
            return "login";
        }
    }
}
