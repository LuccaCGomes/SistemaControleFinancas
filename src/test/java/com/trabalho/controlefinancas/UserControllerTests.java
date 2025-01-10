package com.trabalho.controlefinancas.controller;

import com.trabalho.controlefinancas.model.Category;
import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.service.CategoryService;
import com.trabalho.controlefinancas.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private UserController userController;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testUser");
        testUser.setPassword("password123");
    }

    @Test
    void loggedin_ReturnsLoggedinView() {
        String viewName = userController.loggedin();
        assertEquals("loggedin", viewName);
    }

    @Test
    void home_ReturnsHomeView() {
        String viewName = userController.home();
        assertEquals("home", viewName);
    }

    @Test
    void showRegistrationForm_ReturnsRegisterView() {
        String viewName = userController.showRegistrationForm();
        assertEquals("register", viewName);
    }

    @Test
    void showLoginForm_ReturnsLoginView() {
        String viewName = userController.showLoginForm();
        assertEquals("login", viewName);
    }

    @Test
    void registerUser_SuccessfulRegistration_RedirectsToLogin() {
        // Given
        String username = "newUser";
        String password = "password123";
        when(userService.findUserByUsername(username)).thenReturn(testUser);

        // When
        String viewName = userController.registerUser(username, password, model, redirectAttributes);

        // Then
        assertEquals("redirect:/login", viewName);
        verify(userService).registerUser(username, password);
        verify(categoryService, times(4)).addCategory(any(Category.class));
        verify(redirectAttributes).addFlashAttribute("successMessage", "Cadastro concluído! Faça login.");
    }

    @Test
    void registerUser_UsernameExists_ReturnsRegisterViewWithError() {
        // Given
        String username = "existingUser";
        String password = "password123";
        doThrow(new RuntimeException("Username exists")).when(userService).registerUser(username, password);

        // When
        String viewName = userController.registerUser(username, password, model, redirectAttributes);

        // Then
        assertEquals("register", viewName);
        verify(model).addAttribute("error", "Nome de usuário já existe. Por favor escolha um outro nome de usuário.");
    }

    @Test
    void loginUser_SuccessfulLogin_ReturnsLoggedinView() {
        // Given
        String username = "validUser";
        String password = "validPass";
        when(userService.loginUser(username, password)).thenReturn(true);

        // When
        String viewName = userController.loginUser(username, password, model);

        // Then
        assertEquals("loggedin", viewName);
        verify(model).addAttribute("username", username);
        verify(model).addAttribute("message", "Usuário logado com sucesso!");
    }
}