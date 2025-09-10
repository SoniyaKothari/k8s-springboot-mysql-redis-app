package com.pastelplanner.service;

import com.pastelplanner.model.Expense;
import com.pastelplanner.model.User;
import com.pastelplanner.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    // Save a new expense
    public Expense saveExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    // Get all expenses
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    // Get a single expense by ID
    public Optional<Expense> getExpenseById(Long id) {
        return expenseRepository.findById(id);
    }

    // Delete an expense
    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    // 🔹 Get expenses for a specific user
    public List<Expense> getExpensesByUser(User user) {
        return expenseRepository.findByUser(user);
    }
}
