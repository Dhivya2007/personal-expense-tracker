package com.expensetracker.dao;

import com.expensetracker.model.User;

import java.sql.*;

public class UserDAO {

    public static boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            boolean exists = rs.getInt(1) > 0;
            System.out.println("Username '" + username + "' exists: " + exists);
            return exists;

        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
            return false;
        }
    }

    public static boolean createUser(String username, String password, double monthlyIncome) {
        // First check if username already exists
        if (usernameExists(username)) {
            System.err.println("Username '" + username + "' already exists!");
            return false;
        }

        String sql = "INSERT INTO users (username, password, monthly_income) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setDouble(3, monthlyIncome);

            int affectedRows = pstmt.executeUpdate();
            System.out.println("com.expensetracker.model.User created: " + username + ", Income: " + monthlyIncome + ", Rows affected: " + affectedRows);
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error creating user '" + username + "': " + e.getMessage());
            return false;
        }
    }

    public static User authenticateUser(String username, String password) {
        String sql = "SELECT username, password, monthly_income FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String user = rs.getString("username");
                String pass = rs.getString("password");
                double income = rs.getDouble("monthly_income");

                System.out.println("com.expensetracker.model.User authenticated: " + username);
                return new User(user, pass, income);
            } else {
                System.out.println("Authentication failed for: " + username);
            }

        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }

        return null;
    }

    // Optional: Method to get user by username only
    public static User getUserByUsername(String username) {
        String sql = "SELECT username, password, monthly_income FROM users WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String user = rs.getString("username");
                String pass = rs.getString("password");
                double income = rs.getDouble("monthly_income");

                System.out.println("com.expensetracker.model.User found: " + username);
                return new User(user, pass, income);
            }

        } catch (SQLException e) {
            System.err.println("Error getting user: " + e.getMessage());
        }

        return null;
    }
}