package com.trabalho.controlefinancas.controller;

import com.trabalho.controlefinancas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

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
