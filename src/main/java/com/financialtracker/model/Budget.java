package com.financialtracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "budgets")
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 100)
    private ExpenseCategory category;

    @NotNull
    @Positive(message = "Limit amount must be positive")
    @Column(name = "limit_amount")
    private Double limitAmount;

    @NotNull
    @Column(name = "start_date")
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Using the same categories as Expense for consistency
    public enum ExpenseCategory {
        HOUSING("Housing"),
        TRANSPORTATION("Transportation"),
        FOOD("Food"),
        UTILITIES("Utilities"),
        HEALTHCARE("Healthcare"),
        ENTERTAINMENT("Entertainment"),
        EDUCATION("Education"),
        SHOPPING("Shopping"),
        DEBT("Debt"),
        SAVINGS("Savings"),
        OTHER("Other");

        private final String displayName;

        ExpenseCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
} 