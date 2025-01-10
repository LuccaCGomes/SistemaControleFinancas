package com.trabalho.controlefinancas.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.trabalho.controlefinancas.config.GlobalCurrencyConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trabalho.controlefinancas.model.Category;
import com.trabalho.controlefinancas.model.Transaction;
import com.trabalho.controlefinancas.model.TransactionType;
import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.repository.CategoryRepository;
import com.trabalho.controlefinancas.repository.UserRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CurrencyConversionService currencyConversionService;
    @Autowired
    private GlobalCurrencyConfig globalCurrencyConfig;

    // Injeção de dependência via construtor
    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository, CurrencyConversionService currencyConversionService) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.currencyConversionService = currencyConversionService;
    }

    public void addCategory(Category category) {
        if (category.getUser() != null) {// Associa o usuário à categoria
            categoryRepository.save(category);  // Salva a categoria com o usuário associado
        } else {
            throw new IllegalArgumentException("User cannot be null");
        }
    }
//
//    public List<Category> getAllCategories() {
//        return categoryRepository.findAll();
//    }

    public void deleteCategoryById(Long id){
        Optional<Category> category = categoryRepository.findById(id);
        category.ifPresent(categoryRepository::delete);
    }

    public List<Category> getAllCategoriesByUser(User user) {
        return categoryRepository.findByUser(user);
    }


    /**
     * Verifica se o orçamento da categoria foi excedido.
     *
     * @param user O usuário ao qual a categoria pertence
     * @param categoryId O ID da categoria
     * @return true se o orçamento foi excedido, false caso contrário
     */
    public boolean isBudgetExceededForCategory(User user, Long categoryId) {
        String targetCurrency = globalCurrencyConfig.getDefaultCurrency();
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            if (category.getUser().equals(user)) {
                BigDecimal totalExpenses = category.getTransactions().stream()
                        .filter(transaction -> transaction.getType() == TransactionType.DESPESA)
                        .map(transaction -> {
                            return currencyConversionService.convert(
                                    transaction.getCurrency().name(),
                                    targetCurrency,
                                    transaction.getAmount()
                            );
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                return totalExpenses.compareTo(category.getBudget()) > 0;
            }
        }
        return false;
    }

    /**
     * Retorna o valor restante do orçamento de uma categoria.
     *
     * @param user O usuário ao qual a categoria pertence
     * @param categoryId O ID da categoria
     * @return O valor restante do orçamento, ou BigDecimal.ZERO se a categoria não for encontrada ou o usuário não coincidir
     */
    public BigDecimal getRemainingBudgetForCategory(User user, Long categoryId) {
        String targetCurrency = globalCurrencyConfig.getDefaultCurrency();
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            if (category.getUser().equals(user)) {
                BigDecimal totalExpenses = category.getTransactions().stream()
                        .filter(transaction -> transaction.getType() == TransactionType.DESPESA)
                        .map(transaction -> {
                            return currencyConversionService.convert(
                                    transaction.getCurrency().name(),
                                    targetCurrency,
                                    transaction.getAmount()
                            );
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                return category.getBudget().subtract(totalExpenses);
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Busca uma categoria pelo ID.
     *
     * @param id O ID da categoria.
     * @return A categoria correspondente.
     * @throws IllegalArgumentException Se a categoria não for encontrada.
     */
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada com o ID: " + id));
    }

    /**
     * Atualiza os dados de uma categoria existente.
     *
     * @param category A categoria com os novos dados.
     */
    public void updateCategory(Category category) {
        if (!categoryRepository.existsById(category.getId())) {
            throw new IllegalArgumentException("Categoria não encontrada com o ID: " + category.getId());
        }
        categoryRepository.save(category);
    }

    public Category findByNameAndUser(String name, User user) {
        return categoryRepository.findByNameAndUser(name, user)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada com o Nome e User: " + name + " e " + user.getUsername()));
    }
}

