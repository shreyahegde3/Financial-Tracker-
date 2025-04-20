package com.financialtracker.repository;

import com.financialtracker.model.Budget;
import com.financialtracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUserIdOrderByStartDateDesc(Long userId);
    List<Budget> findByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
        Long userId, LocalDate currentDate, LocalDate currentDate2);
    Optional<Budget> findByUserIdAndCategoryAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
        Long userId, Expense.ExpenseCategory category, LocalDate currentDate, LocalDate currentDate2);
} 