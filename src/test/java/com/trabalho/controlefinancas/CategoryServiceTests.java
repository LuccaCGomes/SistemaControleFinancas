package com.trabalho.controlefinancas;

import com.trabalho.controlefinancas.model.*;
import com.trabalho.controlefinancas.repository.CategoryRepository;
import com.trabalho.controlefinancas.repository.UserRepository;
import com.trabalho.controlefinancas.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTests {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CategoryService categoryService;

    private User user;
    private Category category;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setUsername("userTest");

        category = new Category();
        category.setId(1L);
        category.setName("categoriaTest");
        category.setBudget(new BigDecimal("1000"));
        category.setUser(user);

        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setType(TransactionType.DESPESA);
        transaction.setAmount(new BigDecimal("500"));

        category.setTransactions(List.of(transaction));
    }

    @Test
    void addCategoria_Sucesso() {
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        categoryService.addCategory(category);

        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void addCategoria_NullUserThrowsException() {
        category.setUser(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.addCategory(category);
        });

        assertEquals("User cannot be null", exception.getMessage());
    }

    @Test
    void deletaCategoriaPeloId_CatExiste() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.deleteCategoryById(1L);

        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void deletaCategoriaPeloId_CatNaoExiste() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        categoryService.deleteCategoryById(1L);

        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void getCategoriasPeloUser() {
        when(categoryRepository.findByUser(user)).thenReturn(Arrays.asList(category));

        List<Category> categories = categoryService.getAllCategoriesByUser(user);

        assertEquals(1, categories.size());
        assertEquals("categoriaTest", categories.get(0).getName());
        verify(categoryRepository, times(1)).findByUser(user);
    }

    @Test
    void orcamentoExcedeCategoria() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        boolean isExceeded = categoryService.isBudgetExceededForCategory(user, 1L);

        assertFalse(isExceeded); // Total expenses (500) < Budget (1000)
    }

    @Test
    void orcamentoNaoExcedeCategoria() {
        transaction.setAmount(new BigDecimal("1500"));
        category.setTransactions(List.of(transaction));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        boolean isExceeded = categoryService.isBudgetExceededForCategory(user, 1L);

        assertTrue(isExceeded); // Total expenses (1500) > Budget (1000)
    }

    @Test
    void getOrcamentoRestanteParaCategoria() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        BigDecimal remainingBudget = categoryService.getRemainingBudgetForCategory(user, 1L);

        assertEquals(new BigDecimal("500"), remainingBudget); // Budget (1000) - Expenses (500)
    }

    @Test
    void getOrcamentoRestanteParaCategoria_CategoriaNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        BigDecimal remainingBudget = categoryService.getRemainingBudgetForCategory(user, 1L);

        assertEquals(BigDecimal.ZERO, remainingBudget);
    }
}

