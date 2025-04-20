package com.financialtracker.controller;

import com.financialtracker.model.User;
import com.financialtracker.repository.UserRepository;
import com.financialtracker.repository.IncomeRepository;
import com.financialtracker.repository.ExpenseRepository;
import com.financialtracker.repository.FinancialGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private FinancialGoalRepository goalRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        if (isAuthenticated()) {
            return "redirect:/dashboard";
        }
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        logger.info("Attempting to register user with email: {}", user.getEmail());
        
        if (result.hasErrors()) {
            logger.warn("Validation errors occurred during registration");
            result.getAllErrors().forEach(error -> 
                logger.warn("Validation error: {}", error.getDefaultMessage())
            );
            return "auth/register";
        }

        // Check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            logger.warn("Registration failed: Email already exists - {}", user.getEmail());
            model.addAttribute("error", "Email already exists");
            return "auth/register";
        }

        try {
            // Encrypt password
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            
            // Set creation time
            user.setCreatedAt(java.time.LocalDateTime.now());
            
            // Save user
            User savedUser = userRepository.save(user);
            logger.info("Successfully registered user with ID: {}", savedUser.getId());
            
            return "redirect:/login?registered=true";
        } catch (Exception e) {
            logger.error("Error occurred while saving user: {}", e.getMessage(), e);
            model.addAttribute("error", "An error occurred during registration. Please try again.");
            return "auth/register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        if (isAuthenticated()) {
            return "redirect:/dashboard";
        }
        return "auth/login";
    }

    @GetMapping({"/", "/dashboard"})
    public String showDashboard(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
                return "redirect:/login";
            }

            User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

            // Get current month's data
            YearMonth currentMonth = YearMonth.now();
            LocalDate startOfMonth = currentMonth.atDay(1);
            LocalDate endOfMonth = currentMonth.atEndOfMonth();

            // Calculate totals with null checks
            Double totalIncome = incomeRepository.sumByUserIdAndDateBetween(user.getId(), startOfMonth, endOfMonth);
            Double totalExpenses = expenseRepository.sumByUserIdAndDateBetween(user.getId(), startOfMonth, endOfMonth);
            
            // Handle null values
            totalIncome = totalIncome != null ? totalIncome : 0.0;
            totalExpenses = totalExpenses != null ? totalExpenses : 0.0;
            Double netBalance = totalIncome - totalExpenses;

            // Get goals with null check
            var goals = goalRepository.findByUserIdOrderByTargetDateAsc(user.getId());
            if (goals == null) {
                goals = Collections.emptyList();
            }

            // Add to model
            model.addAttribute("totalIncome", totalIncome);
            model.addAttribute("totalExpenses", totalExpenses);
            model.addAttribute("netBalance", netBalance);
            model.addAttribute("goals", goals);

            return "dashboard";
        } catch (Exception e) {
            logger.error("Error in dashboard: {}", e.getMessage(), e);
            model.addAttribute("error", "An error occurred while loading the dashboard.");
            return "error";
        }
    }

    @GetMapping("/test-db")
    public String testDatabaseConnection(Model model) {
        try {
            // Try to get the count of users
            long userCount = userRepository.count();
            model.addAttribute("status", "success");
            model.addAttribute("message", "Database connection successful! Total users: " + userCount);
        } catch (Exception e) {
            model.addAttribute("status", "error");
            model.addAttribute("message", "Database connection failed: " + e.getMessage());
        }
        return "test-db";
    }

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && 
               authentication.isAuthenticated() && 
               !authentication.getPrincipal().equals("anonymousUser");
    }
} 