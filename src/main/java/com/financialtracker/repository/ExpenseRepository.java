package com.financialtracker.repository;

import com.financialtracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserIdOrderByDateDesc(Long userId);
    List<Expense> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate startDate, LocalDate endDate);
    List<Expense> findByUserIdAndCategory(Long userId, Expense.ExpenseCategory category);
    List<Expense> findByUserIdAndRecurring(Long userId, boolean recurring);
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.id = :userId AND e.date BETWEEN :startDate AND :endDate")
    Double sumByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
} 