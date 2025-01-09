package com.trabalho.controlefinancas.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.trabalho.controlefinancas.model.Category;
import com.trabalho.controlefinancas.model.Transaction;
import com.trabalho.controlefinancas.model.TransactionType;
import com.trabalho.controlefinancas.model.User;
import com.trabalho.controlefinancas.repository.TransactionRepository;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    // Construtor para injeção de dependência
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> getUserTransactions(User user) {
        return transactionRepository.findByUser(user);
    }

    public String addTransaction(Transaction transaction) {
        Category category = transaction.getCategory();
        User user = transaction.getUser();

        // Obtém o mês e o ano da transação
        LocalDate transactionDate = transaction.getDate();
        int month = transactionDate.getMonthValue();
        int year = transactionDate.getYear();


        // Salva a transação mesmo se exceder o limite do mês
        transactionRepository.save(transaction);

        if (transaction.getType() == TransactionType.RECEITA || category.getBudget() == null) {
            return null;
        }
        // se for Receita ou budget não existir
        // o limite do mês não precisa ser calculado


        BigDecimal totalTransactionsAmount = transactionRepository
                .findByCategoryAndUser(category, user)
                .stream()
                .filter(t -> t.getDate().getMonthValue() == month
                        && t.getDate().getYear() == year
                        && t.getType() == TransactionType.DESPESA
                ) // Verifica o mesmo mês, ano e se é uma Despesa
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal newTotal = totalTransactionsAmount.add(transaction.getAmount());



        if (newTotal.compareTo(category.getBudget()) > 0) {

            return "O valor das transações para a categoria " + category.getName() + " excedeu o orçamento mensal.";
        }

        return null;
    }


    public void deleteTransactionByIdAndUser(Long id, User user) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getUser()
                .getId()
                .equals(user.getId())) {
            throw new AccessDeniedException("Not authorized to delete this transaction");
        }

        transactionRepository.delete(transaction);
    }

    public Map<String, BigDecimal> getMonthlyFinancialSummary(User user, int month, int year) {
        List<Transaction> transactions = transactionRepository.findByUser(user);

        // Calcular o saldo inicial (todas as receitas - despesas até o último dia do mês anterior)
        BigDecimal initialBalance = transactions.stream()
                .filter(t -> t.getDate().isBefore(LocalDate.of(year, month, 1)))
                .map(t -> t.getType() == TransactionType.RECEITA ? t.getAmount() : t.getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalIncome = transactions.stream()
                .filter(t -> t.getType() == TransactionType.RECEITA &&
                        t.getDate().getMonthValue() == month &&
                        t.getDate().getYear() == year)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactions.stream()
                .filter(t -> t.getType() == TransactionType.DESPESA &&
                        t.getDate().getMonthValue() == month &&
                        t.getDate().getYear() == year)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal finalBalance = initialBalance.add(totalIncome).subtract(totalExpense);

        Map<String, BigDecimal> summary = new HashMap<>();
        summary.put("initialBalance", initialBalance);
        summary.put("totalIncome", totalIncome);
        summary.put("totalExpense", totalExpense);
        summary.put("finalBalance", finalBalance);

        return summary;
    }

    /**
     * Busca uma transação pelo ID.
     *
     * @param id O ID da transação.
     * @return A transação correspondente.
     * @throws IllegalArgumentException Se a transação não for encontrada.
     */
    public Transaction findById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transação não encontrada com o ID: " + id));
    }

    /**
     * Atualiza os dados de uma transação existente.
     *
     * @param transaction A transação com os novos dados.
     * @throws IllegalArgumentException Se a transação não for encontrada.
     */
    public void updateTransaction(Transaction transaction) {
        if (!transactionRepository.existsById(transaction.getId())) {
            throw new IllegalArgumentException("Transação não encontrada com o ID: " + transaction.getId());
        }
        transactionRepository.save(transaction);
    }

    public List<Transaction> findByMonth(int year, int month, User user){
        return transactionRepository.findTransactionsByMonthAndUser(year, month, user);
    }
}