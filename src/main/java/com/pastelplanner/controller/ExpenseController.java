package com.pastelplanner.controller;

import com.pastelplanner.model.Expense;
import com.pastelplanner.model.User;
import com.pastelplanner.service.ExpenseService;
import com.pastelplanner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private UserService userService;

    // Create expense
    @PostMapping("/create/{userId}")
    public ResponseEntity<?> createExpense(@PathVariable Long userId, @RequestBody Expense expense) {
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body("User not found");
        expense.setUser(userOpt.get());
        Expense savedExpense = expenseService.saveExpense(expense);
        return ResponseEntity.ok(savedExpense);
    }

    // Get all expenses for a user
    @GetMapping("/{userId}")
    public ResponseEntity<?> getExpensesByUser(@PathVariable Long userId) {
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body("User not found");
        List<Expense> expenses = expenseService.getExpensesByUser(userOpt.get());
        return ResponseEntity.ok(expenses);
    }
}
