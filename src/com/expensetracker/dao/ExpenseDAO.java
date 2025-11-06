package com.expensetracker.dao;

import com.expensetracker.model.Expense;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDAO {

    public static boolean addExpense(String username, String description, double amount,
                                     String category, LocalDate date, boolean isRecurring) {
        String sql = "INSERT INTO expenses (username, description, amount, category, expense_date, is_recurring) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, description);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, category);
            pstmt.setString(5, date.toString());
            pstmt.setBoolean(6, isRecurring);

            int result = pstmt.executeUpdate();
            System.out.println("com.expensetracker.model.Expense added for user: " + username + ", Rows affected: " + result);
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Error adding expense: " + e.getMessage());
            return false;
        }
    }

    public static List<Expense> getUserExpenses(String username) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT description, amount, category, expense_date, is_recurring FROM expenses WHERE username = ? ORDER BY expense_date DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String description = rs.getString("description");
                double amount = rs.getDouble("amount");
                String category = rs.getString("category");
                LocalDate date = LocalDate.parse(rs.getString("expense_date"));
                boolean isRecurring = rs.getBoolean("is_recurring");

                expenses.add(new Expense(description, amount, category, date, isRecurring));
            }

            System.out.println("Loaded " + expenses.size() + " expenses for user: " + username);

        } catch (SQLException e) {
            System.err.println("Error loading expenses: " + e.getMessage());
        }

        return expenses;
    }

    public static double getMonthlySpending(String username) {
        String sql = "SELECT SUM(amount) as total FROM expenses WHERE username = ? AND strftime('%Y-%m', expense_date) = strftime('%Y-%m', 'now')";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double total = rs.getDouble("total");
                System.out.println("Monthly spending for " + username + ": $" + total);
                return total;
            }

        } catch (SQLException e) {
            System.err.println("Error calculating monthly spending: " + e.getMessage());
        }

        return 0.0;
    }
}