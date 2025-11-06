package com.expensetracker.main;
import com.expensetracker.modules.AuthModuleSwing;
import com.expensetracker.modules.ExpenseModuleSwing;
import com.expensetracker.modules.SettingsModuleSwing;
import com.expensetracker.dao.DatabaseManager;
import com.expensetracker.dao.UserDAO;
import com.expensetracker.model.User;
import javax.swing.*;
import java.awt.*;

public class MainSwing {
    private static JFrame mainFrame;
    private static String currentUser = null;
    private static double userIncome = 0.0;

    // Modules
    private static AuthModuleSwing authModule = new AuthModuleSwing();
    private static ExpenseModuleSwing expenseModule = new ExpenseModuleSwing();
    private static SettingsModuleSwing settingsModule = new SettingsModuleSwing();

    public static void main(String[] args) {
        // Check if database needs cleanup
        if (DatabaseManager.needsCleanup()) {
            System.out.println("Database needs cleanup...");
            runDatabaseCleanup();
        } else {
            System.out.println("Database is already clean!");
        }

        // Initialize database
        DatabaseManager.initializeDatabase();
        showWelcomeScreen();
    }

    // DATABASE CLEANUP METHOD
    private static void runDatabaseCleanup() {
        int result = JOptionPane.showConfirmDialog(null,
                "🔄 Database Optimization Required!\n\n" +
                        "Your database structure needs to be updated.\n" +
                        "This will:\n" +
                        "• Remove unnecessary columns\n" +
                        "• Fix data formats\n" +
                        "• Preserve all your data\n\n" +
                        "This will only take a few seconds.\n\n" +
                        "Continue?",
                "Database Optimization",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            DatabaseManager.cleanupDatabase();
            JOptionPane.showMessageDialog(null,
                    "✅ Database Optimized Successfully!\n\n" +
                            "• Removed last_login column\n" +
                            "• Fixed table structure\n" +
                            "• Preserved all user accounts\n" +
                            "• Ready to use!",
                    "Optimization Complete",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            // If user cancels, still try to initialize
            JOptionPane.showMessageDialog(null,
                    "⚠️ Using existing database structure.\n" +
                            "Some features may not work correctly.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private static void showWelcomeScreen() {
        JFrame welcomeFrame = new JFrame("Personal Expense Tracker");
        welcomeFrame.setSize(500, 400);
        welcomeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        welcomeFrame.setLayout(new BorderLayout());
        welcomeFrame.setLocationRelativeTo(null);

        JLabel headerLabel = new JLabel("PERSONAL EXPENSE TRACKER", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerLabel.setForeground(new Color(0, 123, 255));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 30, 0));

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        JButton loginBtn = new JButton("LOGIN");
        JButton createAccountBtn = new JButton("CREATE ACCOUNT");

        loginBtn.setBackground(new Color(0, 123, 255));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 16));
        loginBtn.setPreferredSize(new Dimension(200, 50));

        createAccountBtn.setBackground(new Color(40, 167, 69));
        createAccountBtn.setForeground(Color.WHITE);
        createAccountBtn.setFont(new Font("Arial", Font.BOLD, 16));
        createAccountBtn.setPreferredSize(new Dimension(200, 50));

        buttonPanel.add(loginBtn);
        buttonPanel.add(createAccountBtn);

        loginBtn.addActionListener(e -> {
            welcomeFrame.dispose();
            authModule.showLoginScreen(MainSwing::handleLoginSuccess);
        });

        createAccountBtn.addActionListener(e -> {
            welcomeFrame.dispose();
            authModule.showCreateAccountScreen(MainSwing::handleLoginSuccess);
        });

        welcomeFrame.add(headerLabel, BorderLayout.NORTH);
        welcomeFrame.add(buttonPanel, BorderLayout.CENTER);
        welcomeFrame.setVisible(true);
    }

    // Callback for successful login/account creation
    private static void handleLoginSuccess(String username, double income) {
        currentUser = username;
        userIncome = income;
        expenseModule.setUserData(currentUser, userIncome);
        settingsModule.setUserData(currentUser, userIncome);
        showMainDashboard();
    }

    private static void showMainDashboard() {
        mainFrame = new JFrame("Expense Tracker - " + currentUser);
        mainFrame.setSize(900, 700);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout(10, 10));
        mainFrame.setLocationRelativeTo(null);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(248, 249, 250));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel headerLabel = new JLabel("Welcome, " + currentUser + "!", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(33, 37, 41));

        JLabel subHeaderLabel = new JLabel("Income: $" + userIncome, JLabel.CENTER);
        subHeaderLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subHeaderLabel.setForeground(new Color(108, 117, 125));

        headerPanel.add(headerLabel, BorderLayout.NORTH);
        headerPanel.add(subHeaderLabel, BorderLayout.CENTER);

        // Stats Panel
        JPanel statsPanel = expenseModule.createStatsPanel();

        // ALL FEATURES BUTTONS - 3x3 Grid
        JPanel featuresPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        featuresPanel.setBorder(BorderFactory.createTitledBorder("All Features"));
        featuresPanel.setBackground(Color.WHITE);

        String[] buttonNames = {
                "Add Expense", "Add Recurring", "View Expenses",
                "Smart Search", "Analytics", "Savings Progress",
                "Set Budgets", "Export Data", "Logout"
        };

        Color[] buttonColors = {
                new Color(40, 167, 69), new Color(32, 201, 151), new Color(0, 123, 255),
                new Color(111, 66, 193), new Color(253, 126, 20), new Color(255, 193, 7),
                new Color(32, 201, 151), new Color(108, 117, 125), new Color(220, 53, 69)
        };

        for (int i = 0; i < buttonNames.length; i++) {
            JButton button = new JButton("<html><center>" + buttonNames[i] + "</center></html>");
            button.setBackground(buttonColors[i]);
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 12));
            button.setFocusPainted(false);

            final int index = i;
            button.addActionListener(e -> handleFeatureAction(index));
            featuresPanel.add(button);
        }

        // Main layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(statsPanel, BorderLayout.NORTH);
        mainPanel.add(featuresPanel, BorderLayout.CENTER);

        mainFrame.add(headerPanel, BorderLayout.NORTH);
        mainFrame.add(mainPanel, BorderLayout.CENTER);
        mainFrame.setVisible(true);
    }

    private static void handleFeatureAction(int choice) {
        switch (choice) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                expenseModule.handleExpenseOperations(choice, mainFrame);
                break;
            case 6:
            case 7:
            case 8:
                settingsModule.handleSettingsOperations(choice, mainFrame);
                if (choice == 8) {
                    logout();
                }
                break;
        }
    }

    private static void logout() {
        currentUser = null;
        userIncome = 0.0;
        expenseModule.clearUserData();
        settingsModule.clearUserData();
        mainFrame.dispose();
        showWelcomeScreen();
    }

    // Getters for modules
    public static String getCurrentUser() { return currentUser; }
    public static double getUserIncome() { return userIncome; }
    public static JFrame getMainFrame() { return mainFrame; }
}