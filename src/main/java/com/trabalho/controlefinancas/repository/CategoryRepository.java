package com.trabalho.controlefinancas.repository;

import com.trabalho.controlefinancas.model.Category;
import com.trabalho.controlefinancas.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUser(User user);
}
