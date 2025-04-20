package com.financialtracker.facade;

import com.financialtracker.model.User;
import com.financialtracker.model.Expense;
import com.financialtracker.model.Income;
import com.financialtracker.model.Budget;
import com.financialtracker.model.FinancialGoal;
import com.financialtracker.repository.ExpenseRepository;
import com.financialtracker.repository.IncomeRepository;
import com.financialtracker.repository.BudgetRepository;
import com.financialtracker.repository.FinancialGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class FinancialSummaryFacade {
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private IncomeRepository incomeRepository;
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private FinancialGoalRepository financialGoalRepository;

    public FinancialSummary getUserFinancialSummary(Long userId) {
        List<Expense> expenses = expenseRepository.findByUserIdOrderByDateDesc(userId);
        List<Income> incomes = incomeRepository.findByUserIdOrderByDateDesc(userId);
        List<Budget> budgets = budgetRepository.findByUserIdOrderByStartDateDesc(userId);
        List<FinancialGoal> goals = financialGoalRepository.findByUserId(userId);

        BigDecimal totalExpenses = expenses.stream()
                .map(e -> BigDecimal.valueOf(e.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalIncome = incomes.stream()
                .map(i -> BigDecimal.valueOf(i.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new FinancialSummary(totalIncome, totalExpenses, budgets, goals);
    }

    public static class FinancialSummary {
        private BigDecimal totalIncome;
        private BigDecimal totalExpenses;
        private List<Budget> budgets;
        private List<FinancialGoal> goals;

        public FinancialSummary(BigDecimal totalIncome, BigDecimal totalExpenses, List<Budget> budgets, List<FinancialGoal> goals) {
            this.totalIncome = totalIncome;
            this.totalExpenses = totalExpenses;
            this.budgets = budgets;
            this.goals = goals;
        }

        public BigDecimal getTotalIncome() { return totalIncome; }
        public BigDecimal getTotalExpenses() { return totalExpenses; }
        public List<Budget> getBudgets() { return budgets; }
        public List<FinancialGoal> getGoals() { return goals; }
    }
}
