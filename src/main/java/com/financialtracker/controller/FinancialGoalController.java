package com.financialtracker.controller;

import com.financialtracker.model.FinancialGoal;
import com.financialtracker.model.User;
import com.financialtracker.repository.FinancialGoalRepository;
import com.financialtracker.repository.UserRepository;
import com.financialtracker.config.DatabaseConfig;
import com.financialtracker.facade.FinancialSummaryFacade;
import com.financialtracker.proxy.CurrencyConverterProxy;
import com.financialtracker.currency.CurrencyConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/goals")
public class FinancialGoalController {

    @Autowired
    private FinancialGoalRepository goalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DatabaseConfig databaseConfig;

    @Autowired
    private FinancialSummaryFacade financialSummaryFacade;

    @Autowired
    private CurrencyConverterProxy currencyConverterProxy;

    // Homepage route
    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping
    public String showGoalsList(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        
        List<FinancialGoal> goals = goalRepository.findByUserIdOrderByTargetDateAsc(user.getId());
        model.addAttribute("goals", goals);
        model.addAttribute("goal", new FinancialGoal());
        return "goals/list";
    }

    @PostMapping
    public String addGoal(@Valid @ModelAttribute("goal") FinancialGoal goal, BindingResult result) {
        if (result.hasErrors()) {
            return "goals/list";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        FinancialGoal goalToSave = new com.financialtracker.builder.FinancialGoalBuilder()
            .withUser(user)
            .withName(goal.getName())
            .withTargetAmount(goal.getTargetAmount())
            .withCurrentAmount(BigDecimal.ZERO)
            .withTargetDate(goal.getTargetDate())
            .withStatus(FinancialGoal.GoalStatus.IN_PROGRESS)
            .build();
        goalRepository.save(goalToSave);
        return "redirect:/goals";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        
        FinancialGoal goal = goalRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid goal ID: " + id));
            
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Not authorized to edit this goal");
        }
        
        model.addAttribute("goal", goal);
        return "goals/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateGoal(@PathVariable Long id, @Valid @ModelAttribute("goal") FinancialGoal goal, BindingResult result) {
        if (result.hasErrors()) {
            return "goals/edit";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        
        FinancialGoal existingGoal = goalRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid goal ID: " + id));
            
        if (!existingGoal.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Not authorized to edit this goal");
        }
        
        existingGoal.setName(goal.getName());
        existingGoal.setTargetAmount(goal.getTargetAmount());
        existingGoal.setTargetDate(goal.getTargetDate());
        
        goalRepository.save(existingGoal);
        return "redirect:/goals";
    }

    @PostMapping("/{id}/update-progress")
    public String updateProgress(@PathVariable Long id, @RequestParam BigDecimal amount) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        
        FinancialGoal goal = goalRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid goal ID: " + id));
            
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Not authorized to update this goal");
        }
        
        goal.setCurrentAmount(amount);
        
        // Update status if goal is reached
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(FinancialGoal.GoalStatus.COMPLETED);
        }
        
        goalRepository.save(goal);
        return "redirect:/goals";
    }

    @PostMapping("/{id}/delete")
    public String deleteGoal(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        
        FinancialGoal goal = goalRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid goal ID: " + id));
            
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Not authorized to delete this goal");
        }
        
        goalRepository.delete(goal);
        return "redirect:/goals";
    }

    // Endpoint to get financial summary for the logged-in user
    @GetMapping("/summary")
    @ResponseBody
    public FinancialSummaryFacade.FinancialSummary getUserFinancialSummary() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        return financialSummaryFacade.getUserFinancialSummary(user.getId());
    }

    // Example usage of DatabaseConfig in a controller method
    @GetMapping("/db-info")
    @ResponseBody
    public String showDbConfigInfo() {
        return "URL: " + databaseConfig.getUrl() + ", User: " + databaseConfig.getUsername();
    }

    // Example endpoint: Convert INR to USD
    @GetMapping("/convert-currency")
    @ResponseBody
    public String convertCurrency(@RequestParam double amount, @RequestParam String from, @RequestParam String to) {
        double converted = currencyConverterProxy.convert(from, to, amount);
        return String.format("%.2f %s = %.2f %s", amount, from, converted, to);
    }
} 