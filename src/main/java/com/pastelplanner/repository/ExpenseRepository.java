package com.pastelplanner.repository;

import com.pastelplanner.model.Expense;
import com.pastelplanner.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUser(User user);
}
