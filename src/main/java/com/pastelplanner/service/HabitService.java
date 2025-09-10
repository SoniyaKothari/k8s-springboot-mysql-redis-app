package com.pastelplanner.service;

import com.pastelplanner.model.Habit;
import com.pastelplanner.model.User;
import com.pastelplanner.repository.HabitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HabitService {

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String HABIT_KEY_PREFIX = "habits:user:";

    // Save a new habit
    public Habit saveHabit(Habit habit) {
        Habit savedHabit = habitRepository.save(habit);

        // Update Redis cache
        String key = HABIT_KEY_PREFIX + habit.getUser().getId();
        redisTemplate.delete(key); // clear old cache
        redisTemplate.convertAndSend("habitsChannel", "Habit added/updated for user: " + habit.getUser().getId());

        return savedHabit;
    }

    // Get all habits for a user (with Redis cache)
    public List<Habit> getHabitsByUser(User user) {
        String key = HABIT_KEY_PREFIX + user.getId();
        List<Habit> habits = (List<Habit>) redisTemplate.opsForValue().get(key);

        if (habits == null) {
            habits = habitRepository.findByUser(user);
            redisTemplate.opsForValue().set(key, habits); // cache in Redis
        }

        return habits;
    }

    // Find habit by ID
    public Optional<Habit> getHabitById(Long id) {
        return habitRepository.findById(id);
    }

    // Update a habit
    public Habit updateHabit(Habit habit) {
        Habit updatedHabit = habitRepository.save(habit);

        // Update cache & publish
        String key = HABIT_KEY_PREFIX + habit.getUser().getId();
        redisTemplate.delete(key);
        redisTemplate.convertAndSend("habitsChannel", "Habit updated for user: " + habit.getUser().getId());

        return updatedHabit;
    }

    // Delete a habit
    public void deleteHabit(Long id) {
        Optional<Habit> habitOpt = habitRepository.findById(id);
        habitOpt.ifPresent(habit -> {
            habitRepository.deleteById(id);

            // Update cache & publish
            String key = HABIT_KEY_PREFIX + habit.getUser().getId();
            redisTemplate.delete(key);
            redisTemplate.convertAndSend("habitsChannel", "Habit deleted for user: " + habit.getUser().getId());
        });
    }
}
