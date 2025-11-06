package com.expensetracker.dao;

import java.sql.*;
import java.util.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:moneymaster.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        System.out.println("Creating database tables...");

        // ONLY 3 ESSENTIAL TABLES - NO BUDGETS TABLE
        String[] tables = {
                // USERS table - SIMPLE STRUCTURE
                "CREATE TABLE IF NOT EXISTS users (" +
                        "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "username VARCHAR(50) UNIQUE NOT NULL, " +
                        "password VARCHAR(100) NOT NULL, " +
                        "monthly_income DECIMAL(10,2) DEFAULT 0.0, " +
                        "created_date DATE DEFAULT (date('now')))",

                // EXPENSES table - SIMPLE STRUCTURE
                "CREATE TABLE IF NOT EXISTS expenses (" +
                        "expense_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "username VARCHAR(50) NOT NULL, " +
                        "description VARCHAR(200) NOT NULL, " +
                        "amount DECIMAL(10,2) NOT NULL, " +
                        "category VARCHAR(50) NOT NULL, " +
                        "expense_date DATE NOT NULL, " +
                        "is_recurring BOOLEAN DEFAULT FALSE)",

                // CATEGORIES table
                "CREATE TABLE IF NOT EXISTS categories (" +
                        "category_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "category_name VARCHAR(50) NOT NULL, " +
                        "category_icon VARCHAR(10))"
        };

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Create all tables
            for (String tableSql : tables) {
                stmt.execute(tableSql);
            }

            // Insert default categories (NO EMOJIS)
            String[] defaultCategories = {
                    "INSERT OR IGNORE INTO categories (category_name, category_icon) VALUES ('Food', '')",
                    "INSERT OR IGNORE INTO categories (category_name, category_icon) VALUES ('Transport', '')",
                    "INSERT OR IGNORE INTO categories (category_name, category_icon) VALUES ('Entertainment', '')",
                    "INSERT OR IGNORE INTO categories (category_name, category_icon) VALUES ('Utilities', '')",
                    "INSERT OR IGNORE INTO categories (category_name, category_icon) VALUES ('Shopping', '')",
                    "INSERT OR IGNORE INTO categories (category_name, category_icon) VALUES ('Healthcare', '')",
                    "INSERT OR IGNORE INTO categories (category_name, category_icon) VALUES ('Education', '')",
                    "INSERT OR IGNORE INTO categories (category_name, category_icon) VALUES ('Travel', '')",
                    "INSERT OR IGNORE INTO categories (category_name, category_icon) VALUES ('Other', '')"
            };

            for (String insertSql : defaultCategories) {
                stmt.execute(insertSql);
            }

            System.out.println("Database initialized successfully!");
            System.out.println("3 tables created: users, expenses, categories");
            System.out.println("Default categories inserted!");

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void cleanupDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("Cleaning up database structure...");

            // 1. Backup current user data
            System.out.println("Backing up current user data...");
            List<UserData> usersBackup = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery("SELECT username, password, monthly_income FROM users")) {
                while (rs.next()) {
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    double income = rs.getDouble("monthly_income");
                    usersBackup.add(new UserData(username, password, income));
                }
                System.out.println("Found " + usersBackup.size() + " users to backup");
            } catch (SQLException e) {
                System.out.println("No existing user data to backup");
            }

            // 2. Drop ALL tables (including budgets)
            System.out.println("Removing ALL old tables...");
            String[] tablesToDrop = {"budgets", "expenses", "categories", "users"};
            for (String table : tablesToDrop) {
                try {
                    stmt.execute("DROP TABLE IF EXISTS " + table);
                    System.out.println("Dropped table: " + table);
                } catch (SQLException e) {
                    System.out.println("Table " + table + " already dropped");
                }
            }

            // 3. Create NEW clean tables (only 3 tables - NO BUDGETS)
            System.out.println("Creating new clean tables...");
            initializeDatabase(); // This creates the 3 essential tables

            // 4. Restore user data
            if (!usersBackup.isEmpty()) {
                System.out.println("Restoring user data...");
                String insertSQL = "INSERT INTO users (username, password, monthly_income) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                    for (UserData user : usersBackup) {
                        pstmt.setString(1, user.username);
                        pstmt.setString(2, user.password);
                        pstmt.setDouble(3, user.income);
                        pstmt.executeUpdate();
                    }
                }
                System.out.println("Restored " + usersBackup.size() + " users");
            }

            System.out.println("Database cleanup completed successfully!");

        } catch (SQLException e) {
            System.err.println("Database cleanup failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to check if cleanup is needed
    public static boolean needsCleanup() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Check if budgets table exists (we want to remove it)
            ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='budgets'");
            if (rs.next()) {
                return true; // Budgets table exists, need cleanup
            }

            // Check if users table has last_login column
            rs = stmt.executeQuery("PRAGMA table_info(users)");
            while (rs.next()) {
                String columnName = rs.getString("name");
                if ("last_login".equals(columnName)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            // If we can't check, assume cleanup is needed
            return true;
        }
        return false;
    }

    // Inner class to hold user data for backup
    private static class UserData {
        String username;
        String password;
        double income;

        UserData(String username, String password, double income) {
            this.username = username;
            this.password = password;
            this.income = income;
        }
    }
}