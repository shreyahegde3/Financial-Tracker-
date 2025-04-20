package com.financialtracker.factory;

import com.financialtracker.model.Income;
import com.financialtracker.model.Expense;
import com.financialtracker.model.Budget;
import com.financialtracker.model.User;
import java.time.LocalDate;

public class TransactionFactory {
    
    public static Income createIncome(User user, Double amount, Income.IncomeSource source, 
                                    Income.IncomeCategory category, LocalDate date, boolean recurring) {
        Income income = new Income();
        income.setUser(user);
        income.setAmount(amount);
        income.setSource(source);
        income.setCategory(category);
        income.setDate(date);
        income.setRecurring(recurring);
        return income;
    }
    
    public static Expense createExpense(User user, Double amount, Expense.ExpenseCategory category,
                                      String description, LocalDate date, boolean recurring) {
        Expense expense = new Expense();
        expense.setUser(user);
        expense.setAmount(amount);
        expense.setCategory(category);
        expense.setDescription(description);
        expense.setDate(date);
        expense.setRecurring(recurring);
        return expense;
    }
    
    public static Budget createBudget(User user, Budget.ExpenseCategory category, Double limitAmount,
                                    LocalDate startDate, LocalDate endDate) {
        Budget budget = new Budget();
        budget.setUser(user);
        budget.setCategory(category);
        budget.setLimitAmount(limitAmount);
        budget.setStartDate(startDate);
        budget.setEndDate(endDate);
        return budget;
    }
} 