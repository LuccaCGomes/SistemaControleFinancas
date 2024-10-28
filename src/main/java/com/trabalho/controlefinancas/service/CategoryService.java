package com.trabalho.controlefinancas.service;

import com.trabalho.controlefinancas.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.trabalho.controlefinancas.model.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public void addCategory(Category category) {
        categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
