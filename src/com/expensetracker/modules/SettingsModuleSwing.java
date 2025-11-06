package com.expensetracker.modules;
import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.model.Expense;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SettingsModuleSwing extends BaseModule{
    //private String currentUser;
    private double savingsGoal = 0.0;
    private Double overallBudget = null;
    private Map<String, Double> categoryBudgets = new HashMap<>();
    @Override
    public void setUserData(String username, double income) {
        super.setUserData(username, income);
        // Settings module doesn't need income, but we have to accept it
    }
    @Override
    public void clearUserData() {
        super.clearUserData();
        this.savingsGoal = 0.0;
        this.overallBudget = null;
        this.categoryBudgets.clear();
    }


    public void handleSettingsOperations(int choice, JFrame parentFrame) {
        switch (choice) {
            case 6: showSetBudgets(parentFrame); break;
            case 7: showExportData(parentFrame); break;
            case 8:
                JOptionPane.showMessageDialog(parentFrame, "Logging out...");
                break;
        }
    }

    private void showSetBudgets(JFrame parentFrame) {
        JDialog budgetsDialog = new JDialog(parentFrame, "Set Budgets & Goals", true);
        budgetsDialog.setSize(400, 400);
        budgetsDialog.setLayout(new GridLayout(5, 1, 10, 10));
        budgetsDialog.setLocationRelativeTo(parentFrame);

        JButton monthlyBudgetBtn = new JButton("Set Monthly Budget");
        JButton categoryBudgetBtn = new JButton("Set Category Budgets");
        JButton savingsGoalBtn = new JButton("Set Savings Goal");
        JButton viewSettingsBtn = new JButton("View Current Settings");
        JButton closeBtn = new JButton("Close");

        monthlyBudgetBtn.setBackground(new Color(32, 201, 151));
        monthlyBudgetBtn.setForeground(Color.WHITE);
        categoryBudgetBtn.setBackground(new Color(32, 201, 151));
        categoryBudgetBtn.setForeground(Color.WHITE);
        savingsGoalBtn.setBackground(new Color(32, 201, 151));
        savingsGoalBtn.setForeground(Color.WHITE);
        viewSettingsBtn.setBackground(new Color(108, 117, 125));
        viewSettingsBtn.setForeground(Color.WHITE);
        closeBtn.setBackground(Color.GRAY);
        closeBtn.setForeground(Color.WHITE);

        monthlyBudgetBtn.addActionListener(e -> {
            budgetsDialog.dispose();
            showSetMonthlyBudget(parentFrame);
        });
        categoryBudgetBtn.addActionListener(e -> {
            budgetsDialog.dispose();
            showSetCategoryBudgets(parentFrame);
        });
        savingsGoalBtn.addActionListener(e -> {
            budgetsDialog.dispose();
            showSetSavingsGoal(parentFrame);
        });
        viewSettingsBtn.addActionListener(e -> {
            budgetsDialog.dispose();
            showCurrentSettings(parentFrame);
        });
        closeBtn.addActionListener(e -> budgetsDialog.dispose());

        budgetsDialog.add(monthlyBudgetBtn);
        budgetsDialog.add(categoryBudgetBtn);
        budgetsDialog.add(savingsGoalBtn);
        budgetsDialog.add(viewSettingsBtn);
        budgetsDialog.add(closeBtn);
        budgetsDialog.setVisible(true);
    }

    private void showSetMonthlyBudget(JFrame parentFrame) {
        String input = JOptionPane.showInputDialog(parentFrame, "Enter overall monthly budget:");
        if (input != null && !input.isEmpty()) {
            try {
                overallBudget = Double.parseDouble(input);
                JOptionPane.showMessageDialog(parentFrame, "Monthly budget set to $" + overallBudget);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parentFrame, "Please enter a valid number!");
            }
        }
    }

    private void showSetCategoryBudgets(JFrame parentFrame) {
        JDialog categoryDialog = new JDialog(parentFrame, "Set Category Budget", true);
        categoryDialog.setSize(300, 200);
        categoryDialog.setLayout(new GridLayout(3, 2, 10, 10));
        categoryDialog.setLocationRelativeTo(parentFrame);

        JLabel categoryLabel = new JLabel("Category:");
        JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"Food", "Transport", "Entertainment", "Utilities", "Shopping", "Healthcare", "Other"});
        JLabel budgetLabel = new JLabel("Budget:");
        JTextField budgetField = new JTextField();
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.setBackground(new Color(32, 201, 151));
        saveBtn.setForeground(Color.WHITE);
        cancelBtn.setBackground(Color.GRAY);
        cancelBtn.setForeground(Color.WHITE);

        saveBtn.addActionListener(e -> {
            try {
                String category = (String) categoryCombo.getSelectedItem();
                double budget = Double.parseDouble(budgetField.getText());
                categoryBudgets.put(category, budget);
                JOptionPane.showMessageDialog(categoryDialog, category + " budget set to $" + budget);
                categoryDialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(categoryDialog, "Please enter a valid number!");
            }
        });
        cancelBtn.addActionListener(e -> categoryDialog.dispose());

        categoryDialog.add(categoryLabel);
        categoryDialog.add(categoryCombo);
        categoryDialog.add(budgetLabel);
        categoryDialog.add(budgetField);
        categoryDialog.add(saveBtn);
        categoryDialog.add(cancelBtn);
        categoryDialog.setVisible(true);
    }

    private void showSetSavingsGoal(JFrame parentFrame) {
        String input = JOptionPane.showInputDialog(parentFrame, "Enter monthly savings goal:");
        if (input != null && !input.isEmpty()) {
            try {
                savingsGoal = Double.parseDouble(input);
                JOptionPane.showMessageDialog(parentFrame,
                        "Savings goal set to $" + savingsGoal + "\nYou can do it! Every dollar counts!");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parentFrame, "Please enter a valid number!");
            }
        }
    }

    private void showCurrentSettings(JFrame parentFrame) {
        StringBuilder settings = new StringBuilder();
        settings.append("CURRENT FINANCIAL SETTINGS:\n\n");
        settings.append("Monthly Budget: $").append(overallBudget != null ? String.format("%.2f", overallBudget) : "Not set").append("\n");
        settings.append("Savings Goal: $").append(String.format("%.2f", savingsGoal)).append("\n\n");
        settings.append("CATEGORY BUDGETS:\n");

        if (categoryBudgets.isEmpty()) {
            settings.append("  No category budgets set\n");
        } else {
            for (Map.Entry<String, Double> entry : categoryBudgets.entrySet()) {
                settings.append("  ").append(entry.getKey()).append(": $").append(String.format("%.2f", entry.getValue())).append("\n");
            }
        }

        JOptionPane.showMessageDialog(parentFrame, settings.toString(), "Current Settings", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showExportData(JFrame parentFrame) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Expenses to CSV");

        // Set default to user's documents folder
        File documentsDir = new File(System.getProperty("user.home"), "Documents");
        if (documentsDir.exists()) {
            fileChooser.setCurrentDirectory(documentsDir);
        }

        fileChooser.setSelectedFile(new File("my_finances.csv"));
        fileChooser.setApproveButtonText("Export");

        int userSelection = fileChooser.showSaveDialog(parentFrame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // Ensure .csv extension
            if (!fileToSave.getName().toLowerCase().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".csv");
            }

            try (java.io.PrintWriter writer = new java.io.PrintWriter(fileToSave)) {
                // Get expenses from database
                var expenses = ExpenseDAO.getUserExpenses(currentUser);

                writer.println("Date,Category,Description,Amount");
                for (Expense expense : expenses) {
                    writer.printf("%s,%s,%s,%.2f%n",
                            expense.getDate(),
                            expense.getCategory(),
                            expense.getDescription().replace(",", ";"),
                            expense.getAmount());
                }

                // Show success message with file path
                String message = String.format(
                        "Data exported successfully!\n\n" +
                                "File: %s\n" +
                                "Location: %s\n\n" +
                                "Total records: %d expenses",
                        fileToSave.getName(),
                        fileToSave.getParent(),
                        expenses.size()
                );

                JOptionPane.showMessageDialog(parentFrame,
                        message,
                        "Export Successful",
                        JOptionPane.INFORMATION_MESSAGE);

                // Option to open the folder
                int openFolder = JOptionPane.showConfirmDialog(parentFrame,
                        "Would you like to open the export folder?",
                        "Open Folder",
                        JOptionPane.YES_NO_OPTION);

                if (openFolder == JOptionPane.YES_OPTION) {
                    try {
                        // Open folder in file explorer
                        Desktop.getDesktop().open(fileToSave.getParentFile());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(parentFrame,
                                "Cannot open folder: " + ex.getMessage(),
                                "Folder Error",
                                JOptionPane.WARNING_MESSAGE);
                    }
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame,
                        "Export failed: " + ex.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}