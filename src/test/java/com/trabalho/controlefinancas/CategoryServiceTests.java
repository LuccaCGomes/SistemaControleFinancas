package com.trabalho.controlefinancas;

import com.trabalho.controlefinancas.model.Category;
import com.trabalho.controlefinancas.model.Transaction;
import com.trabalho.controlefinancas.model.TransactionType;
import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.repository.CategoryRepository;
import com.trabalho.controlefinancas.repository.UserRepository;
import com.trabalho.controlefinancas.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void addCategory_ValidCategory_SavesCategory() {
        Category category = new Category();
        User user = new User();
        category.setUser(user);

        categoryService.addCategory(category);

        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void addCategory_NullUser_ThrowsException() {
        Category category = new Category();
        category.setUser(null);

        assertThrows(IllegalArgumentException.class, () -> categoryService.addCategory(category));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deleteCategoryById_ExistingCategory_DeletesCategory() {
        Long categoryId = 1L;
        Category category = new Category();
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        categoryService.deleteCategoryById(categoryId);

        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void deleteCategoryById_NonExistingCategory_NoAction() {
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        categoryService.deleteCategoryById(categoryId);

        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void getAllCategoriesByUser_ValidUser_ReturnsCategories() {
        User user = new User();
        List<Category> expectedCategories = List.of(new Category(), new Category());
        when(categoryRepository.findByUser(user)).thenReturn(expectedCategories);

        List<Category> result = categoryService.getAllCategoriesByUser(user);

        assertEquals(expectedCategories, result);
        verify(categoryRepository, times(1)).findByUser(user);
    }

    @Test
    void isBudgetExceededForCategory_ValidCategoryNotExceeded_ReturnsFalse() {
        User user = new User();
        Long categoryId = 1L;
        Category category = new Category();
        category.setUser(user);
        category.setBudget(new BigDecimal("1000"));

        List<Transaction> transactions = new ArrayList<>();
        Transaction t1 = new Transaction();
        t1.setType(TransactionType.DESPESA);
        t1.setAmount(new BigDecimal("500"));
        transactions.add(t1);
        category.setTransactions(transactions);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        boolean result = categoryService.isBudgetExceededForCategory(user, categoryId);

        assertFalse(result);
    }

    @Test
    void isBudgetExceededForCategory_ValidCategoryExceeded_ReturnsTrue() {
        User user = new User();
        Long categoryId = 1L;
        Category category = new Category();
        category.setUser(user);
        category.setBudget(new BigDecimal("1000"));

        List<Transaction> transactions = new ArrayList<>();
        Transaction t1 = new Transaction();
        t1.setType(TransactionType.DESPESA);
        t1.setAmount(new BigDecimal("1500"));
        transactions.add(t1);
        category.setTransactions(transactions);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        boolean result = categoryService.isBudgetExceededForCategory(user, categoryId);

        assertTrue(result);
    }

    @Test
    void isBudgetExceededForCategory_CategoryNotFound_ReturnsFalse() {
        User user = new User();
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        boolean result = categoryService.isBudgetExceededForCategory(user, categoryId);

        assertFalse(result);
    }

    @Test
    void isBudgetExceededForCategory_DifferentUser_ReturnsFalse() {
        User user1 = new User();
        User user2 = new User();
        Long categoryId = 1L;
        Category category = new Category();
        category.setUser(user2);
        category.setBudget(new BigDecimal("1000"));

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        boolean result = categoryService.isBudgetExceededForCategory(user1, categoryId);

        assertFalse(result);
    }

    @Test
    void getRemainingBudgetForCategory_ValidCategory_ReturnsCorrectAmount() {
        User user = new User();
        Long categoryId = 1L;
        Category category = new Category();
        category.setUser(user);
        category.setBudget(new BigDecimal("1000"));

        List<Transaction> transactions = new ArrayList<>();
        Transaction t1 = new Transaction();
        t1.setType(TransactionType.DESPESA);
        t1.setAmount(new BigDecimal("600"));
        transactions.add(t1);
        category.setTransactions(transactions);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        BigDecimal result = categoryService.getRemainingBudgetForCategory(user, categoryId);

        assertEquals(new BigDecimal("400"), result);
    }

    @Test
    void getRemainingBudgetForCategory_CategoryNotFound_ReturnsZero() {
        User user = new User();
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        BigDecimal result = categoryService.getRemainingBudgetForCategory(user, categoryId);

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void findById_ExistingCategory_ReturnsCategory() {
        Long categoryId = 1L;
        Category category = new Category();
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        Category result = categoryService.findById(categoryId);

        assertEquals(category, result);
    }

    @Test
    void findById_NonExistingCategory_ThrowsException() {
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> categoryService.findById(categoryId));
    }

    @Test
    void updateCategory_ExistingCategory_UpdatesCategory() {
        Category category = new Category();
        category.setId(1L);
        when(categoryRepository.existsById(1L)).thenReturn(true);

        categoryService.updateCategory(category);

        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void updateCategory_NonExistingCategory_ThrowsException() {
        Category category = new Category();
        category.setId(1L);
        when(categoryRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> categoryService.updateCategory(category));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void findByNameAndUser_ExistingCategory_ReturnsCategory() {
        String categoryName = "Test";
        User user = new User();
        Category category = new Category();
        when(categoryRepository.findByNameAndUser(categoryName, user)).thenReturn(Optional.of(category));

        Category result = categoryService.findByNameAndUser(categoryName, user);

        assertEquals(category, result);
    }

    @Test
    void findByNameAndUser_NonExistingCategory_ThrowsException() {
        String categoryName = "Test";
        User user = new User();
        user.setUsername("testUser");
        when(categoryRepository.findByNameAndUser(categoryName, user)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> categoryService.findByNameAndUser(categoryName, user));
    }
}