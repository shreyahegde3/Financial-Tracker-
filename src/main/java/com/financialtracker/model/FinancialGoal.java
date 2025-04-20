package com.financialtracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "financial_goals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FinancialGoal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goal_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Goal name is required")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "Target amount is required")
    @Positive(message = "Target amount must be positive")
    @Column(name = "target_amount", nullable = false)
    private BigDecimal targetAmount;

    @Column(name = "current_amount")
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @NotNull(message = "Target date is required")
    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private GoalStatus status = GoalStatus.IN_PROGRESS;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        if (currentAmount == null) {
            currentAmount = BigDecimal.ZERO;
        }
        if (status == null) {
            status = GoalStatus.IN_PROGRESS;
        }
    }

    public enum GoalStatus {
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
} 