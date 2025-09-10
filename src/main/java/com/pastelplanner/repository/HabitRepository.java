package com.pastelplanner.repository;

import com.pastelplanner.model.Habit;
import com.pastelplanner.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {
    // Find all habits for a specific user
    List<Habit> findByUser(User user);
}
