package com.financialtracker.flyweight;

import com.financialtracker.model.Expense;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Flyweight Factory for ExpenseCategory enum instances.
 * Ensures that only one instance of each category is used throughout the app.
 */
public class ExpenseCategoryFlyweightFactory {
    private static final Map<String, Expense.ExpenseCategory> CATEGORY_MAP = new ConcurrentHashMap<>();

    static {
        for (Expense.ExpenseCategory cat : Expense.ExpenseCategory.values()) {
            CATEGORY_MAP.put(cat.name(), cat);
        }
    }

    public static Expense.ExpenseCategory getCategory(String name) {
        return CATEGORY_MAP.getOrDefault(name, Expense.ExpenseCategory.OTHER);
    }
}
