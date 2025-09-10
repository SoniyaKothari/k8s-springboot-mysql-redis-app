package com.pastelplanner.controller;

import com.pastelplanner.model.Habit;
import com.pastelplanner.model.Expense;
import com.pastelplanner.model.User;
import com.pastelplanner.repository.HabitRepository;
import com.pastelplanner.repository.ExpenseRepository;
import com.pastelplanner.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class DashboardController {

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HttpSession session;

    // ---------------- Habits ----------------

    // Get all habits for current user
    @GetMapping("/habits")
    public List<Habit> getHabits() {
        User user = getCurrentUser();
        if (user == null) {
            throw new RuntimeException("User not authenticated");
        }
        return habitRepository.findByUser(user);
    }

    // Add new habit
    @PostMapping("/habits")
    public Habit addHabit(@RequestBody Map<String, String> body) {
        User user = getCurrentUser();
        if (user == null) {
            throw new RuntimeException("User not authenticated");
        }
        String title = body.get("title");
        Habit habit = new Habit();
        habit.setTitle(title);
        habit.setCompleted(false);
        habit.setUser(user);
        return habitRepository.save(habit);
    }

    // Toggle habit completion
    @PostMapping("/habits/{id}/toggle")
    public Habit toggleHabit(@PathVariable Long id) {
        Habit habit = habitRepository.findById(id).orElseThrow(() -> new RuntimeException("Habit not found"));
        habit.setCompleted(!habit.isCompleted());
        return habitRepository.save(habit);
    }

    // ---------------- Expenses ----------------

    // Get all expenses for current user
    @GetMapping("/expenses")
    public List<Expense> getExpenses() {
        User user = getCurrentUser();
        if (user == null) {
            throw new RuntimeException("User not authenticated");
        }
        return expenseRepository.findByUser(user);
    }

    // Add new expense
    @PostMapping("/expenses")
    public Expense addExpense(@RequestBody Map<String, Object> body) {
        User user = getCurrentUser();
        if (user == null) {
            throw new RuntimeException("User not authenticated");
        }
        String name = (String) body.get("name");
        double amount = Double.parseDouble(body.get("amount").toString());
        Expense expense = new Expense();
        expense.setName(name);
        expense.setAmount(amount);
        expense.setUser(user);
        return expenseRepository.save(expense);
    }

    // ---------------- Summary ----------------

    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        User user = getCurrentUser();
        if (user == null) {
            throw new RuntimeException("User not authenticated");
        }
        List<Habit> habits = habitRepository.findByUser(user);
        List<Expense> expenses = expenseRepository.findByUser(user);

        long completedHabits = habits.stream().filter(Habit::isCompleted).count();
        double totalExpense = expenses.stream().mapToDouble(Expense::getAmount).sum();

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalHabits", habits.size());
        summary.put("completedHabits", completedHabits);
        summary.put("totalExpense", totalExpense);

        return summary;
    }

    // ---------------- Helper ----------------

    // Get current user from session
    private User getCurrentUser() {
        return (User) session.getAttribute("user");
    }
}