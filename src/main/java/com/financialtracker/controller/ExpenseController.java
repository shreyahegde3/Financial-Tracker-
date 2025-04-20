package com.financialtracker.controller;

import com.financialtracker.model.Expense;
import com.financialtracker.model.User;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {
    
    private static final Logger logger = LoggerFactory.getLogger(ExpenseController.class);

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String showExpenseList(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userRepository.findByEmail(auth.getName()).orElseThrow();
            
            List<Expense> expenses = expenseRepository.findByUserIdOrderByDateDesc(user.getId());
            model.addAttribute("expenses", expenses);
            model.addAttribute("expense", new Expense());
            return "expenses/list";
        } catch (Exception e) {
            logger.error("Error in showExpenseList: ", e);
            throw e;
        }
    }

    @PostMapping
    public String addExpense(@Valid @ModelAttribute("expense") Expense expense, BindingResult result) {
        try {
            if (result.hasErrors()) {
                logger.error("Validation errors: {}", result.getAllErrors());
                return "expenses/list";
            }

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userRepository.findByEmail(auth.getName()).orElseThrow();
            
            // Use TransactionFactory to create the expense
            Expense newExpense = TransactionFactory.createExpense(
                user,
                expense.getAmount(),
                expense.getCategory(),
                expense.getDescription(),
                expense.getDate() != null ? expense.getDate() : LocalDate.now(),
                expense.isRecurring()
            );
            
            expenseRepository.save(newExpense);
            return "redirect:/expenses";
        } catch (Exception e) {
            logger.error("Error in addExpense: ", e);
            throw e;
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            Expense expense = expenseRepository.findById(id).orElseThrow();
            model.addAttribute("expense", expense);
            return "expenses/edit";
        } catch (Exception e) {
            logger.error("Error in showEditForm: ", e);
            throw e;
        }
    }

    @PostMapping("/{id}/edit")
    public String updateExpense(@PathVariable Long id, @Valid @ModelAttribute("expense") Expense expense, BindingResult result) {
        try {
            if (result.hasErrors()) {
                logger.error("Validation errors in update: {}", result.getAllErrors());
                return "expenses/edit";
            }

            Expense existingExpense = expenseRepository.findById(id).orElseThrow();
            
            // Use TransactionFactory to create updated expense
            Expense updatedExpense = TransactionFactory.createExpense(
                existingExpense.getUser(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getDescription(),
                expense.getDate(),
                expense.isRecurring()
            );
            
            // Preserve the ID
            updatedExpense.setId(existingExpense.getId());
            
            expenseRepository.save(updatedExpense);
            return "redirect:/expenses";
        } catch (Exception e) {
            logger.error("Error in updateExpense: ", e);
            throw e;
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteExpense(@PathVariable Long id) {
        try {
            expenseRepository.deleteById(id);
            return "redirect:/expenses";
        } catch (Exception e) {
            logger.error("Error in deleteExpense: ", e);
            throw e;
        }
    }
} 