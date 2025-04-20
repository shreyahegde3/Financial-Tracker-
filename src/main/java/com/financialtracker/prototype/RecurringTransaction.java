package com.financialtracker.prototype;

import com.financialtracker.model.Income;
import com.financialtracker.model.Expense;
import java.time.LocalDate;

public interface RecurringTransaction<T> {
    T clone();
    void setDate(LocalDate date);
}

class RecurringIncome implements RecurringTransaction<Income> {
    private Income income;
    
    public RecurringIncome(Income income) {
        this.income = income;
    }
    
    @Override
    public Income clone() {
        Income newIncome = new Income();
        newIncome.setUser(income.getUser());
        newIncome.setAmount(income.getAmount());
        newIncome.setSource(income.getSource());
        newIncome.setCategory(income.getCategory());
        newIncome.setRecurring(income.isRecurring());
        return newIncome;
    }
    
    @Override
    public void setDate(LocalDate date) {
        income.setDate(date);
    }
}

class RecurringExpense implements RecurringTransaction<Expense> {
    private Expense expense;
    
    public RecurringExpense(Expense expense) {
        this.expense = expense;
    }
    
    @Override
    public Expense clone() {
        Expense newExpense = new Expense();
        newExpense.setUser(expense.getUser());
        newExpense.setAmount(expense.getAmount());
        newExpense.setCategory(expense.getCategory());
        newExpense.setDescription(expense.getDescription());
        newExpense.setRecurring(expense.isRecurring());
        return newExpense;
    }
    
    @Override
    public void setDate(LocalDate date) {
        expense.setDate(date);
    }
} 