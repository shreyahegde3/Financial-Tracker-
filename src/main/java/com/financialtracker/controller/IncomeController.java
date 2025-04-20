package com.financialtracker.controller;

import com.financialtracker.model.Income;
import com.financialtracker.model.User;
import com.financialtracker.repository.IncomeRepository;
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

@Controller
@RequestMapping("/income")
public class IncomeController {

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String showIncomeList(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        
        List<Income> incomes = incomeRepository.findByUserIdOrderByDateDesc(user.getId());
        model.addAttribute("incomes", incomes);
        model.addAttribute("income", new Income());
        return "income/list";
    }

    @PostMapping
    public String addIncome(@Valid @ModelAttribute("income") Income income, BindingResult result) {
        if (result.hasErrors()) {
            return "income/list";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        
        // Use TransactionFactory to create the income
        Income newIncome = TransactionFactory.createIncome(
            user,
            income.getAmount(),
            income.getSource(),
            income.getCategory(),
            income.getDate() != null ? income.getDate() : LocalDate.now(),
            income.isRecurring()
        );
        
        incomeRepository.save(newIncome);
        return "redirect:/income";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Income income = incomeRepository.findById(id).orElseThrow();
        model.addAttribute("income", income);
        return "income/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateIncome(@PathVariable Long id, @Valid @ModelAttribute("income") Income income, BindingResult result) {
        if (result.hasErrors()) {
            return "income/edit";
        }

        Income existingIncome = incomeRepository.findById(id).orElseThrow();
        
        // Use TransactionFactory to create updated income
        Income updatedIncome = TransactionFactory.createIncome(
            existingIncome.getUser(),
            income.getAmount(),
            income.getSource(),
            income.getCategory(),
            income.getDate(),
            income.isRecurring()
        );
        
        // Preserve the ID
        updatedIncome.setId(existingIncome.getId());
        
        incomeRepository.save(updatedIncome);
        return "redirect:/income";
    }

    @PostMapping("/{id}/delete")
    public String deleteIncome(@PathVariable Long id) {
        incomeRepository.deleteById(id);
        return "redirect:/income";
    }
} 