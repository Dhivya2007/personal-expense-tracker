package com.expensetracker.model;

public class User {
    private String username;
    private String password;
    private double monthlyIncome;

    public User(String username, String password, double monthlyIncome) {
        this.username = username;
        this.password = password;
        this.monthlyIncome = monthlyIncome;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public double getMonthlyIncome() {
        return monthlyIncome;
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMonthlyIncome(double monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    @Override
    public String toString() {
        return "com.expensetracker.model.User{" +
                "username='" + username + '\'' +
                ", monthlyIncome=" + monthlyIncome +
                '}';
    }
}