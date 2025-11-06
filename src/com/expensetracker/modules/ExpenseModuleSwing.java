package com.expensetracker.modules;
import com.expensetracker.model.Expense;
import com.expensetracker.dao.ExpenseDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ExpenseModuleSwing extends BaseModule {
    private List<Expense> expenses = new ArrayList<>();

    @Override
    public void setUserData(String username, double income) {
        super.setUserData(username, income);
        loadUserData();
    }

    @Override
    public void clearUserData() {
        super.clearUserData();
        expenses.clear();
    }

    public JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        double spentThisMonth = getCurrentMonthSpending();
        double remaining = userIncome - spentThisMonth;
        double savingsRate = userIncome > 0 ? (remaining / userIncome) * 100 : 0;

        JPanel incomeCard = createStatCard("Monthly Income", "$" + String.format("%.2f", userIncome), new Color(40, 167, 69));
        JPanel spentCard = createStatCard("Spent This Month", "$" + String.format("%.2f", spentThisMonth), new Color(220, 53, 69));
        JPanel remainingCard = createStatCard("Remaining", "$" + String.format("%.2f", remaining), new Color(0, 123, 255));
        JPanel savingsCard = createStatCard("Savings Rate", String.format("%.1f%%", savingsRate), new Color(255, 193, 7));

        statsPanel.add(incomeCard);
        statsPanel.add(spentCard);
        statsPanel.add(remainingCard);
        statsPanel.add(savingsCard);

        return statsPanel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(color);

        JLabel valueLabel = new JLabel(value, JLabel.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        valueLabel.setForeground(color);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    public void handleExpenseOperations(int choice, JFrame parentFrame) {
        switch (choice) {
            case 0: showAddExpenseWithAI(parentFrame); break;
            case 1: showAddRecurringExpense(parentFrame); break;
            case 2: showViewExpenses(parentFrame); break;
            case 3: showSmartSearch(parentFrame); break;
            case 4: showVisualAnalytics(parentFrame); break;
            case 5: showSavingsProgress(parentFrame); break;
        }
    }

    private void showAddExpenseWithAI(JFrame parentFrame) {
        JDialog expenseDialog = new JDialog(parentFrame, "Add com.expensetracker.model.Expense with AI", true);
        expenseDialog.setSize(500, 400);
        expenseDialog.setLayout(new GridLayout(7, 2, 10, 10));
        expenseDialog.setLocationRelativeTo(parentFrame);

        JLabel titleLabel = new JLabel("AI EXPENSE ASSISTANT", JLabel.CENTER);
        JLabel descLabel = new JLabel("Description:");
        JTextField descField = new JTextField();
        JLabel aiLabel = new JLabel("AI Suggestion:", JLabel.CENTER);
        JLabel suggestedCategoryLabel = new JLabel("", JLabel.CENTER);
        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField();
        JLabel dateLabel = new JLabel("Date:");
        JTextField dateField = new JTextField(LocalDate.now().toString());
        JLabel categoryLabel = new JLabel("Category:");
        JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"Food", "Transport", "Entertainment", "Utilities", "Shopping", "Healthcare", "Other"});
        JButton saveBtn = new JButton("Save com.expensetracker.model.Expense");
        JLabel statusLabel = new JLabel("", JLabel.CENTER);

        // AI Suggestion on typing
        descField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateAISuggestion(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateAISuggestion(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateAISuggestion(); }

            private void updateAISuggestion() {
                String suggestion = suggestCategory(descField.getText());
                suggestedCategoryLabel.setText("Suggested: " + suggestion);
                suggestedCategoryLabel.setForeground(Color.BLUE);
            }
        });

        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        saveBtn.setBackground(new Color(40, 167, 69));
        saveBtn.setForeground(Color.WHITE);

        expenseDialog.add(titleLabel);
        expenseDialog.add(new JLabel());
        expenseDialog.add(descLabel);
        expenseDialog.add(descField);
        expenseDialog.add(aiLabel);
        expenseDialog.add(suggestedCategoryLabel);
        expenseDialog.add(amountLabel);
        expenseDialog.add(amountField);
        expenseDialog.add(dateLabel);
        expenseDialog.add(dateField);
        expenseDialog.add(categoryLabel);
        expenseDialog.add(categoryCombo);
        expenseDialog.add(saveBtn);
        expenseDialog.add(statusLabel);

        saveBtn.addActionListener(e -> {
            try {
                String description = descField.getText();
                double amount = Double.parseDouble(amountField.getText());
                String category = (String) categoryCombo.getSelectedItem();
                LocalDate date = LocalDate.parse(dateField.getText());

                if (description.isEmpty()) {
                    statusLabel.setText("Enter description!");
                    statusLabel.setForeground(Color.RED);
                    return;
                }

                boolean success = ExpenseDAO.addExpense(currentUser, description, amount, category, date, false);

                if (success) {
                    statusLabel.setText("com.expensetracker.model.Expense saved to database!");
                    statusLabel.setForeground(Color.GREEN);
                    loadUserData();

                    // FIXED: Use javax.swing.Timer explicitly
                    javax.swing.Timer timer = new javax.swing.Timer(1000, ev -> expenseDialog.dispose());
                    timer.setRepeats(false);
                    timer.start();
                } else {
                    statusLabel.setText("Failed to save expense!");
                    statusLabel.setForeground(Color.RED);
                }
            } catch (Exception ex) {
                statusLabel.setText("Invalid amount or date!");
                statusLabel.setForeground(Color.RED);
            }
        });

        expenseDialog.setVisible(true);
    }

    private String suggestCategory(String description) {
        description = description.toLowerCase();
        if (description.contains("food") || description.contains("restaurant") || description.contains("grocery")) return "Food";
        if (description.contains("bus") || description.contains("train") || description.contains("taxi") || description.contains("uber")) return "Transport";
        if (description.contains("movie") || description.contains("game") || description.contains("netflix") || description.contains("spotify")) return "Entertainment";
        if (description.contains("bill") || description.contains("electric") || description.contains("water") || description.contains("internet")) return "Utilities";
        if (description.contains("shirt") || description.contains("shoe") || description.contains("shop") || description.contains("mall")) return "Shopping";
        return "Other";
    }

    private void showAddRecurringExpense(JFrame parentFrame) {
        JDialog recurringDialog = new JDialog(parentFrame, "Add Recurring com.expensetracker.model.Expense", true);
        recurringDialog.setSize(400, 350);
        recurringDialog.setLayout(new GridLayout(6, 2, 10, 10));
        recurringDialog.setLocationRelativeTo(parentFrame);

        JLabel descLabel = new JLabel("Description:");
        JTextField descField = new JTextField();
        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField();
        JLabel categoryLabel = new JLabel("Category:");
        JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"Food", "Transport", "Entertainment", "Utilities", "Shopping", "Other"});
        JLabel dateLabel = new JLabel("Start Date:");
        JTextField dateField = new JTextField(LocalDate.now().toString());
        JButton saveBtn = new JButton("Save Recurring");
        JLabel statusLabel = new JLabel("", JLabel.CENTER);

        saveBtn.setBackground(new Color(32, 201, 151));
        saveBtn.setForeground(Color.WHITE);

        recurringDialog.add(descLabel);
        recurringDialog.add(descField);
        recurringDialog.add(amountLabel);
        recurringDialog.add(amountField);
        recurringDialog.add(categoryLabel);
        recurringDialog.add(categoryCombo);
        recurringDialog.add(dateLabel);
        recurringDialog.add(dateField);
        recurringDialog.add(saveBtn);
        recurringDialog.add(statusLabel);

        saveBtn.addActionListener(e -> {
            try {
                String description = descField.getText();
                double amount = Double.parseDouble(amountField.getText());
                String category = (String) categoryCombo.getSelectedItem();
                LocalDate date = LocalDate.parse(dateField.getText());

                if (description.isEmpty()) {
                    statusLabel.setText("Enter description!");
                    statusLabel.setForeground(Color.RED);
                    return;
                }

                boolean success = ExpenseDAO.addExpense(currentUser, description, amount, category, date, true);

                if (success) {
                    statusLabel.setText("Recurring expense saved!");
                    statusLabel.setForeground(Color.GREEN);
                    loadUserData();
                    // FIXED: Use javax.swing.Timer explicitly
                    javax.swing.Timer timer = new javax.swing.Timer(1000, ev -> recurringDialog.dispose());
                    timer.setRepeats(false);
                    timer.start();
                } else {
                    statusLabel.setText("Failed to save!");
                    statusLabel.setForeground(Color.RED);
                }
            } catch (Exception ex) {
                statusLabel.setText("Invalid amount or date!");
                statusLabel.setForeground(Color.RED);
            }
        });

        recurringDialog.setVisible(true);
    }

    private void showViewExpenses(JFrame parentFrame) {
        JFrame viewFrame = new JFrame("Your Expenses");
        viewFrame.setSize(800, 600);
        viewFrame.setLocationRelativeTo(parentFrame);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Date", "Category", "Description", "Amount"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        Map<LocalDate, List<Expense>> expensesByDate = expenses.stream()
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .collect(Collectors.groupingBy(Expense::getDate));

        for (Map.Entry<LocalDate, List<Expense>> entry : expensesByDate.entrySet()) {
            model.addRow(new Object[]{"=== " + entry.getKey() + " ===", "", "", ""});

            double dayTotal = 0;
            for (Expense expense : entry.getValue()) {
                model.addRow(new Object[]{
                        "", expense.getCategory(), expense.getDescription(),
                        String.format("$%.2f", expense.getAmount())
                });
                dayTotal += expense.getAmount();
            }
            model.addRow(new Object[]{"", "Daily Total:", "", String.format("$%.2f", dayTotal)});
            model.addRow(new Object[]{"", "", "", ""});
        }

        JTable expenseTable = new JTable(model);
        expenseTable.setRowHeight(25);

        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
        JLabel totalLabel = new JLabel("GRAND TOTAL: $" + String.format("%.2f", total), JLabel.RIGHT);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));

        mainPanel.add(new JScrollPane(expenseTable), BorderLayout.CENTER);
        mainPanel.add(totalLabel, BorderLayout.SOUTH);

        viewFrame.add(mainPanel);
        viewFrame.setVisible(true);
    }

    // SMART SEARCH METHODS
    private void showSmartSearch(JFrame parentFrame) {
        JDialog searchDialog = new JDialog(parentFrame, "Smart Search", true);
        searchDialog.setSize(400, 300);
        searchDialog.setLayout(new GridLayout(5, 1, 10, 10));
        searchDialog.setLocationRelativeTo(parentFrame);

        JButton descSearchBtn = new JButton("Search by Description");
        JButton categorySearchBtn = new JButton("Search by Category");
        JButton amountSearchBtn = new JButton("Search by Amount Range");
        JButton dateSearchBtn = new JButton("Search by Date Range");
        JButton cancelBtn = new JButton("Cancel");

        descSearchBtn.setBackground(new Color(111, 66, 193));
        descSearchBtn.setForeground(Color.WHITE);
        categorySearchBtn.setBackground(new Color(111, 66, 193));
        categorySearchBtn.setForeground(Color.WHITE);
        amountSearchBtn.setBackground(new Color(111, 66, 193));
        amountSearchBtn.setForeground(Color.WHITE);
        dateSearchBtn.setBackground(new Color(111, 66, 193));
        dateSearchBtn.setForeground(Color.WHITE);
        cancelBtn.setBackground(Color.GRAY);
        cancelBtn.setForeground(Color.WHITE);

        descSearchBtn.addActionListener(e -> {
            searchDialog.dispose();
            showDescriptionSearch(parentFrame);
        });
        categorySearchBtn.addActionListener(e -> {
            searchDialog.dispose();
            showCategorySearch(parentFrame);
        });
        amountSearchBtn.addActionListener(e -> {
            searchDialog.dispose();
            showAmountSearch(parentFrame);
        });
        dateSearchBtn.addActionListener(e -> {
            searchDialog.dispose();
            showDateSearch(parentFrame);
        });
        cancelBtn.addActionListener(e -> searchDialog.dispose());

        searchDialog.add(descSearchBtn);
        searchDialog.add(categorySearchBtn);
        searchDialog.add(amountSearchBtn);
        searchDialog.add(dateSearchBtn);
        searchDialog.add(cancelBtn);
        searchDialog.setVisible(true);
    }

    private void showDescriptionSearch(JFrame parentFrame) {
        String keyword = JOptionPane.showInputDialog(parentFrame, "Enter keyword to search:");
        if (keyword != null && !keyword.isEmpty()) {
            List<Expense> results = expenses.stream()
                    .filter(e -> e.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
            showSearchResults("Description Search: " + keyword, results, parentFrame);
        }
    }

    private void showCategorySearch(JFrame parentFrame) {
        String[] categories = {"Food", "Transport", "Entertainment", "Utilities", "Shopping", "Healthcare", "Other"};
        String category = (String) JOptionPane.showInputDialog(parentFrame, "Select category:", "Category Search",
                JOptionPane.QUESTION_MESSAGE, null, categories, categories[0]);
        if (category != null) {
            List<Expense> results = expenses.stream()
                    .filter(e -> e.getCategory().equalsIgnoreCase(category))
                    .collect(Collectors.toList());
            showSearchResults("Category Search: " + category, results, parentFrame);
        }
    }

    private void showAmountSearch(JFrame parentFrame) {
        JDialog amountDialog = new JDialog(parentFrame, "Amount Range Search", true);
        amountDialog.setSize(300, 200);
        amountDialog.setLayout(new GridLayout(3, 2, 10, 10));
        amountDialog.setLocationRelativeTo(parentFrame);

        JLabel minLabel = new JLabel("Min Amount:");
        JTextField minField = new JTextField();
        JLabel maxLabel = new JLabel("Max Amount:");
        JTextField maxField = new JTextField();
        JButton searchBtn = new JButton("Search");
        JButton cancelBtn = new JButton("Cancel");

        searchBtn.setBackground(new Color(111, 66, 193));
        searchBtn.setForeground(Color.WHITE);
        cancelBtn.setBackground(Color.GRAY);
        cancelBtn.setForeground(Color.WHITE);

        searchBtn.addActionListener(e -> {
            try {
                double min = Double.parseDouble(minField.getText());
                double max = Double.parseDouble(maxField.getText());
                List<Expense> results = expenses.stream()
                        .filter(exp -> exp.getAmount() >= min && exp.getAmount() <= max)
                        .collect(Collectors.toList());
                amountDialog.dispose();
                showSearchResults("Amount Range: $" + min + " - $" + max, results, parentFrame);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(amountDialog, "Please enter valid numbers!");
            }
        });
        cancelBtn.addActionListener(e -> amountDialog.dispose());

        amountDialog.add(minLabel);
        amountDialog.add(minField);
        amountDialog.add(maxLabel);
        amountDialog.add(maxField);
        amountDialog.add(searchBtn);
        amountDialog.add(cancelBtn);
        amountDialog.setVisible(true);
    }

    private void showDateSearch(JFrame parentFrame) {
        JDialog dateDialog = new JDialog(parentFrame, "Date Range Search", true);
        dateDialog.setSize(300, 200);
        dateDialog.setLayout(new GridLayout(3, 2, 10, 10));
        dateDialog.setLocationRelativeTo(parentFrame);

        JLabel startLabel = new JLabel("Start Date (YYYY-MM-DD):");
        JTextField startField = new JTextField(LocalDate.now().minusDays(30).toString());
        JLabel endLabel = new JLabel("End Date (YYYY-MM-DD):");
        JTextField endField = new JTextField(LocalDate.now().toString());
        JButton searchBtn = new JButton("Search");
        JButton cancelBtn = new JButton("Cancel");

        searchBtn.setBackground(new Color(111, 66, 193));
        searchBtn.setForeground(Color.WHITE);
        cancelBtn.setBackground(Color.GRAY);
        cancelBtn.setForeground(Color.WHITE);

        searchBtn.addActionListener(e -> {
            try {
                LocalDate start = LocalDate.parse(startField.getText());
                LocalDate end = LocalDate.parse(endField.getText());
                List<Expense> results = expenses.stream()
                        .filter(exp -> !exp.getDate().isBefore(start) && !exp.getDate().isAfter(end))
                        .collect(Collectors.toList());
                dateDialog.dispose();
                showSearchResults("Date Range: " + start + " to " + end, results, parentFrame);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dateDialog, "Please enter valid dates (YYYY-MM-DD)!");
            }
        });
        cancelBtn.addActionListener(e -> dateDialog.dispose());

        dateDialog.add(startLabel);
        dateDialog.add(startField);
        dateDialog.add(endLabel);
        dateDialog.add(endField);
        dateDialog.add(searchBtn);
        dateDialog.add(cancelBtn);
        dateDialog.setVisible(true);
    }

    private void showSearchResults(String title, List<Expense> results, JFrame parentFrame) {
        JFrame resultsFrame = new JFrame("Search Results - " + title);
        resultsFrame.setSize(600, 400);
        resultsFrame.setLocationRelativeTo(parentFrame);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (results.isEmpty()) {
            JLabel noResults = new JLabel("No expenses found matching your criteria.", JLabel.CENTER);
            noResults.setFont(new Font("Arial", Font.BOLD, 16));
            mainPanel.add(noResults, BorderLayout.CENTER);
        } else {
            String[] columns = {"Date", "Category", "Description", "Amount"};
            DefaultTableModel model = new DefaultTableModel(columns, 0);

            for (Expense expense : results) {
                model.addRow(new Object[]{
                        expense.getDate(),
                        expense.getCategory(),
                        expense.getDescription(),
                        String.format("$%.2f", expense.getAmount())
                });
            }

            JTable resultsTable = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(resultsTable);

            JLabel countLabel = new JLabel("Found " + results.size() + " expenses", JLabel.CENTER);
            countLabel.setFont(new Font("Arial", Font.BOLD, 14));

            mainPanel.add(scrollPane, BorderLayout.CENTER);
            mainPanel.add(countLabel, BorderLayout.SOUTH);
        }

        resultsFrame.add(mainPanel);
        resultsFrame.setVisible(true);
    }

    // VISUAL ANALYTICS
    private void showVisualAnalytics(JFrame parentFrame) {
        JFrame analyticsFrame = new JFrame("Financial Analytics");
        analyticsFrame.setSize(700, 600);
        analyticsFrame.setLocationRelativeTo(parentFrame);

        JTextArea analyticsArea = new JTextArea();
        analyticsArea.setEditable(false);
        analyticsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        StringBuilder sb = new StringBuilder();
        sb.append("FINANCIAL ANALYTICS DASHBOARD\n");
        sb.append("==============================\n\n");

        if (expenses.isEmpty()) {
            sb.append("No data for analytics yet!\n");
        } else {
            // Category breakdown with progress bars (like console)
            Map<String, Double> categorySpending = expenses.stream()
                    .collect(Collectors.groupingBy(Expense::getCategory, Collectors.summingDouble(Expense::getAmount)));

            double total = expenses.stream().mapToDouble(Expense::getAmount).sum();

            sb.append("CATEGORY BREAKDOWN:\n");
            sb.append("-------------------\n");

            categorySpending.entrySet().stream()
                    .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                    .forEach(entry -> {
                        double percentage = (entry.getValue() / total) * 100;
                        int bars = (int) (percentage / 2);
                        String bar = "█".repeat(bars) + "░".repeat(50 - bars);
                        sb.append(String.format("%-15s %s %6.1f%% $%.2f\n",
                                entry.getKey(), bar, percentage, entry.getValue()));
                    });

            sb.append("\nMONTHLY TREND:\n");
            sb.append("-------------\n");

            Map<String, Double> monthlySpending = expenses.stream()
                    .collect(Collectors.groupingBy(
                            e -> e.getDate().getMonth().toString() + " " + e.getDate().getYear(),
                            Collectors.summingDouble(Expense::getAmount)
                    ));

            monthlySpending.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        sb.append(String.format("  %-15s: $%.2f\n", entry.getKey(), entry.getValue()));
                    });

            sb.append("\nTOTAL SPENT: $").append(String.format("%.2f", total)).append("\n");
        }

        analyticsArea.setText(sb.toString());
        analyticsFrame.add(new JScrollPane(analyticsArea));
        analyticsFrame.setVisible(true);
    }

    // SAVINGS PROGRESS
    private void showSavingsProgress(JFrame parentFrame) {
        // For now, just show a message that this feature needs savings goal from settings
        JOptionPane.showMessageDialog(parentFrame,
                "Please set your savings goal in the Settings menu first!\n\n" +
                        "Go to: Set Budgets → Set Savings Goal",
                "Savings Progress",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadUserData() {
        if (currentUser != null) {
            expenses = ExpenseDAO.getUserExpenses(currentUser);
        }
    }

    private double getCurrentMonthSpending() {
        if (currentUser != null) {
            return ExpenseDAO.getMonthlySpending(currentUser);
        }
        return 0.0;
    }
}