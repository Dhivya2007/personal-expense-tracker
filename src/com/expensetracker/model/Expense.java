package com.expensetracker.model;

import java.time.LocalDate;

public class Expense {
    private String description;
    private double amount;
    private String category;
    private LocalDate date;
    private boolean isRecurring;

    // Constructor with ALL parameters including isRecurring
    public Expense(String description, double amount, String category, LocalDate date, boolean isRecurring) {
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.isRecurring = isRecurring;
    }

    // Constructor without isRecurring (for backward compatibility)
    public Expense(String description, double amount, String category, LocalDate date) {
        this(description, amount, category, date, false);
    }

    // Getters
    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    // Setters
    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }

    @Override
    public String toString() {
        return "com.expensetracker.model.Expense{" +
                "description='" + description + '\'' +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", date=" + date +
                ", isRecurring=" + isRecurring +
                '}';
    }
}