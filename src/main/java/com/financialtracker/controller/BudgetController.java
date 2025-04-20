package com.financialtracker.controller;

import com.financialtracker.model.Budget;
import com.financialtracker.model.Expense;
import com.financialtracker.model.User;
import com.financialtracker.repository.BudgetRepository;
import com.financialtracker.repository.ExpenseRepository;
import com.financialtracker.repository.UserRepository;
import com.financialtracker.factory.TransactionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/budgets")
public class BudgetController {

    private static final Logger logger = LoggerFactory.getLogger(BudgetController.class);

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String showBudgetList(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userRepository.findByEmail(auth.getName()).orElseThrow();
            
            List<Budget> budgets = budgetRepository.findByUserIdOrderByStartDateDesc(user.getId());
            List<Expense> expenses = expenseRepository.findByUserIdOrderByDateDesc(user.getId());
            
            // Calculate total expenses by category for the current month
            LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
            LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
            
            Map<Budget.ExpenseCategory, Double> expensesByCategory = expenses.stream()
                .filter(e -> !e.getDate().isBefore(startOfMonth) && !e.getDate().isAfter(endOfMonth))
                .collect(Collectors.groupingBy(
                    e -> Budget.ExpenseCategory.valueOf(e.getCategory().name()),
                    Collectors.summingDouble(Expense::getAmount)
                ));

            model.addAttribute("budgets", budgets);
            model.addAttribute("budget", new Budget());
            model.addAttribute("expensesByCategory", expensesByCategory);
            return "budgets/list";
        } catch (Exception e) {
            logger.error("Error in showBudgetList: ", e);
            model.addAttribute("error", "Failed to load budgets: " + e.getMessage());
            return "budgets/list";
        }
    }

    @PostMapping
    public String addBudget(@Valid @ModelAttribute("budget") Budget budget, BindingResult result, Model model) {
        try {
            if (result.hasErrors()) {
                logger.error("Validation errors: {}", result.getAllErrors());
                // Add the budgets list back to the model
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                User user = userRepository.findByEmail(auth.getName()).orElseThrow();
                List<Budget> budgets = budgetRepository.findByUserIdOrderByStartDateDesc(user.getId());
                model.addAttribute("budgets", budgets);
                return "budgets/list";
            }

            // Validate date range
            if (budget.getEndDate().isBefore(budget.getStartDate())) {
                result.rejectValue("endDate", "error.endDate", "End date must be after start date");
                return "budgets/list";
            }

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userRepository.findByEmail(auth.getName()).orElseThrow();
            
            // Use TransactionFactory to create the budget
            Budget newBudget = TransactionFactory.createBudget(
                user,
                budget.getCategory(),
                budget.getLimitAmount(),
                budget.getStartDate(),
                budget.getEndDate()
            );
            
            logger.info("Saving budget: {}", newBudget);
            budgetRepository.save(newBudget);
            return "redirect:/budgets";
        } catch (Exception e) {
            logger.error("Error in addBudget: ", e);
            model.addAttribute("error", "Failed to add budget: " + e.getMessage());
            // Add the budgets list back to the model
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userRepository.findByEmail(auth.getName()).orElseThrow();
            List<Budget> budgets = budgetRepository.findByUserIdOrderByStartDateDesc(user.getId());
            model.addAttribute("budgets", budgets);
            return "budgets/list";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            Budget budget = budgetRepository.findById(id).orElseThrow();
            model.addAttribute("budget", budget);
            return "budgets/edit";
        } catch (Exception e) {
            logger.error("Error in showEditForm: ", e);
            model.addAttribute("error", "Failed to load budget: " + e.getMessage());
            return "redirect:/budgets";
        }
    }

    @PostMapping("/{id}/edit")
    public String updateBudget(@PathVariable Long id, @Valid @ModelAttribute("budget") Budget budget, BindingResult result, Model model) {
        try {
            if (result.hasErrors()) {
                logger.error("Validation errors in update: {}", result.getAllErrors());
                return "budgets/edit";
            }

            // Validate date range
            if (budget.getEndDate().isBefore(budget.getStartDate())) {
                result.rejectValue("endDate", "error.endDate", "End date must be after start date");
                return "budgets/edit";
            }

            Budget existingBudget = budgetRepository.findById(id).orElseThrow();
            
            // Use TransactionFactory to create updated budget
            Budget updatedBudget = TransactionFactory.createBudget(
                existingBudget.getUser(),
                budget.getCategory(),
                budget.getLimitAmount(),
                budget.getStartDate(),
                budget.getEndDate()
            );
            
            // Preserve the ID
            updatedBudget.setId(existingBudget.getId());
            
            budgetRepository.save(updatedBudget);
            return "redirect:/budgets";
        } catch (Exception e) {
            logger.error("Error in updateBudget: ", e);
            model.addAttribute("error", "Failed to update budget: " + e.getMessage());
            return "budgets/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteBudget(@PathVariable Long id, Model model) {
        try {
            budgetRepository.deleteById(id);
            return "redirect:/budgets";
        } catch (Exception e) {
            logger.error("Error in deleteBudget: ", e);
            model.addAttribute("error", "Failed to delete budget: " + e.getMessage());
            return "redirect:/budgets";
        }
    }
} 