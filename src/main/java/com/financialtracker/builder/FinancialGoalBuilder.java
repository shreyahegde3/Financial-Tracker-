package com.financialtracker.builder;

import com.financialtracker.model.FinancialGoal;
import com.financialtracker.model.User;
import java.math.BigDecimal;
import java.time.LocalDate;

public class FinancialGoalBuilder {
    private FinancialGoal goal;
    
    public FinancialGoalBuilder() {
        goal = new FinancialGoal();
    }
    
    public FinancialGoalBuilder withUser(User user) {
        goal.setUser(user);
        return this;
    }
    
    public FinancialGoalBuilder withName(String name) {
        goal.setName(name);
        return this;
    }
    
    public FinancialGoalBuilder withTargetAmount(BigDecimal targetAmount) {
        goal.setTargetAmount(targetAmount);
        return this;
    }
    
    public FinancialGoalBuilder withCurrentAmount(BigDecimal currentAmount) {
        goal.setCurrentAmount(currentAmount);
        return this;
    }
    
    public FinancialGoalBuilder withTargetDate(LocalDate targetDate) {
        goal.setTargetDate(targetDate);
        return this;
    }
    
    public FinancialGoalBuilder withStatus(FinancialGoal.GoalStatus status) {
        goal.setStatus(status);
        return this;
    }
    
    public FinancialGoal build() {
        // Validate required fields
        if (goal.getUser() == null || goal.getName() == null || 
            goal.getTargetAmount() == null || goal.getTargetDate() == null) {
            throw new IllegalStateException("Required fields are missing");
        }
        
        // Set default values if not set
        if (goal.getCurrentAmount() == null) {
            goal.setCurrentAmount(BigDecimal.ZERO);
        }
        if (goal.getStatus() == null) {
            goal.setStatus(FinancialGoal.GoalStatus.IN_PROGRESS);
        }
        
        return goal;
    }
} 