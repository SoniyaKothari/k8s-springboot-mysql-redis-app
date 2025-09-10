package com.pastelplanner.controller;

import com.pastelplanner.model.Habit;
import com.pastelplanner.model.Expense;
import com.pastelplanner.model.User;
import com.pastelplanner.service.HabitService;
import com.pastelplanner.service.ExpenseService;
import com.pastelplanner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/summary")
public class SummaryController {

    @Autowired
    private HabitService habitService;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public Map<String, Object> getSummary(@PathVariable Long userId) {
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) throw new RuntimeException("User not found");
        User user = userOpt.get();

        List<Habit> habits = habitService.getHabitsByUser(user);
        List<Expense> expenses = expenseService.getExpensesByUser(user);

        long completed = habits.stream().filter(Habit::isCompleted).count();
        double totalExpense = expenses.stream().mapToDouble(Expense::getAmount).sum();

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalHabits", habits.size() + completed); // include completed removed habits
        summary.put("completedHabits", completed);
        summary.put("totalExpense", totalExpense);

        return summary;
    }
}
