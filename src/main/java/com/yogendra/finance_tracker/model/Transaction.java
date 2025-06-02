package com.yogendra.finance_tracker.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Category category;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String type; // "INCOME" or "EXPENSE"

    private String description;

    @Column(nullable = false)
    private LocalDate date;
}
