package com.pastelplanner.controller;

import com.pastelplanner.model.Habit;
import com.pastelplanner.model.User;
import com.pastelplanner.service.HabitService;
import com.pastelplanner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/habits")
public class HabitController {

    @Autowired
    private HabitService habitService;

    @Autowired
    private UserService userService;

    // Create habit
    @PostMapping("/create/{userId}")
    public ResponseEntity<?> createHabit(@PathVariable Long userId, @RequestBody Habit habit) {
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body("User not found");
        habit.setUser(userOpt.get());
        habit.setCompleted(false); // new habit always starts as active
        Habit savedHabit = habitService.saveHabit(habit);
        return ResponseEntity.ok(savedHabit);
    }

    // Get active habits for a user
    @GetMapping("/{userId}")
    public ResponseEntity<?> getHabitsByUser(@PathVariable Long userId) {
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body("User not found");
        List<Habit> habits = habitService.getHabitsByUser(userOpt.get());
        habits.removeIf(Habit::isCompleted); // filter out completed
        return ResponseEntity.ok(habits);
    }

    // ✅ Get completed habits for a user
    @GetMapping("/{userId}/completed")
    public ResponseEntity<?> getCompletedHabits(@PathVariable Long userId) {
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body("User not found");
        List<Habit> habits = habitService.getHabitsByUser(userOpt.get());
        habits.removeIf(h -> !h.isCompleted()); // keep only completed
        return ResponseEntity.ok(habits);
    }

    // Mark habit as done
    @DeleteMapping("/{habitId}/done")
    public ResponseEntity<?> markHabitDone(@PathVariable Long habitId) {
        Optional<Habit> habitOpt = habitService.getHabitById(habitId);
        if (habitOpt.isEmpty()) return ResponseEntity.notFound().build();

        Habit habit = habitOpt.get();
        habit.setCompleted(true); // mark as completed
        habitService.updateHabit(habit); // update in DB
        return ResponseEntity.ok("Habit marked as completed");
    }
}
