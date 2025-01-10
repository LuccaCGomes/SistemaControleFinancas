package com.trabalho.controlefinancas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trabalho.controlefinancas.model.Category;
import com.trabalho.controlefinancas.model.User;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUser(User user);
    Optional<Category> findByNameAndUser(String name, User user);
}
