package com.trabalho.controlefinancas.service;

import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.repository.CategoryRepository;
import com.trabalho.controlefinancas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.trabalho.controlefinancas.model.Category;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

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
}
