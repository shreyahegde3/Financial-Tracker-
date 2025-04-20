package com.financialtracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "expenses")
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 100)
    private ExpenseCategory category;

    private String description;

    @NotNull
    private LocalDate date;

    @NotNull
    @Column(name = "is_recurring")
    private boolean recurring;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

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

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 