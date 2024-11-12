package com.trabalho.controlefinancas.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column
    private BigDecimal budget;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Relacionamento com a tabela User
    private User user;

    // Default constructor
    public Category() {}

    // Constructor with name
    public Category(String name) {
        this.name = name;
    }

    // Constructor with name and budget
    public Category(String name, BigDecimal budget) {
        this.name = name;
        this.budget = budget;
    }

    // Constructor with name and description
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Constructor with name, description and budget
    public Category(String name, String description, BigDecimal budget) {
        this.name = name;
        this.description = description;
        this.budget = budget;
    }

    // Constructor with name, description, budget and user
    public Category(String name, String description, BigDecimal budget, User user) {
        this.name = name;
        this.description = description;
        this.budget = budget;
        this.user = user;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Helper method to add a transaction
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        transaction.setCategory(this);
    }

    // Helper method to remove a transaction
    public void removeTransaction(Transaction transaction) {
        transactions.remove(transaction);
        transaction.setCategory(null);
    }
}
