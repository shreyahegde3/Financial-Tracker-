package com.financialtracker.repository;

import com.financialtracker.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findByUserIdOrderByDateDesc(Long userId);
    List<Income> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate startDate, LocalDate endDate);
    List<Income> findByUserIdAndRecurring(Long userId, boolean recurring);
    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i WHERE i.user.id = :userId AND i.date BETWEEN :startDate AND :endDate")
    Double sumByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
} 