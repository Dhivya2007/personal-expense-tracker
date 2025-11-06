import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.model.Expense;
import com.expensetracker.model.User;

import java.util.*;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class ExpenseModule {
    private User currentUser;
    private List<Expense> expenses = new ArrayList<>();

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            loadUserExpenses();
        }
    }

    public void displayFinancialOverview() {
        if (currentUser == null) return;

        double monthlyIncome = currentUser.getMonthlyIncome();
        double spentThisMonth = getCurrentMonthSpending();
        double remainingMoney = monthlyIncome - spentThisMonth;
        double savingsRate = (remainingMoney / monthlyIncome) * 100;

        System.out.println(" com.expensetracker.model.User: " + currentUser.getUsername() + " | Income: $" + monthlyIncome);
        System.out.println(" FINANCIAL OVERVIEW:");
        System.out.printf("   Spent this month: $%.2f\n", spentThisMonth);
        System.out.printf("   Remaining money: $%.2f\n", remainingMoney);
        System.out.printf("   Savings rate: %.1f%%\n", savingsRate);
    }

    public void handleExpenseOperations(int choice, Scanner scanner) {
        switch (choice) {
            case 1: addExpenseWithAI(scanner); break;
            case 2: addRecurringExpense(scanner); break;
            case 3: viewExpensesWithStyle(); break;
            case 4: smartSearch(scanner); break;
        }
    }

    public void handleFinancialInsights(int choice, Scanner scanner) {
        switch (choice) {
            case 5: showVisualAnalytics(); break;
            case 6: showSavingsProgress(scanner); break;
        }
    }

    private void addExpenseWithAI(Scanner scanner) {
        System.out.println("\n  AI com.expensetracker.model.Expense Assistant");
        System.out.println("Tip: I can suggest categories based on your description!");

        System.out.print(" Description: ");
        String description = scanner.nextLine();

        String suggestedCategory = suggestCategory(description);
        System.out.println(" Suggested Category: " + suggestedCategory);

        System.out.print(" Amount: $");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        System.out.print(" Date (YYYY-MM-DD) or ENTER for today: ");
        String dateInput = scanner.nextLine();
        LocalDate date = dateInput.isEmpty() ? LocalDate.now() : LocalDate.parse(dateInput);

        System.out.print("Category (or ENTER for suggested): ");
        String categoryInput = scanner.nextLine();
        String category = categoryInput.isEmpty() ? suggestedCategory : categoryInput;

        if (ExpenseDAO.addExpense(currentUser.getUsername(), description, amount, category, date, false)) {
            System.out.println("  com.expensetracker.model.Expense added to database!");
            loadUserExpenses();
        } else {
            showError("Failed to save expense to database!");
        }
    }

    private String suggestCategory(String description) {
        description = description.toLowerCase();
        if (description.contains("food") || description.contains("restaurant") || description.contains("grocery")) return "Food";
        if (description.contains("bus") || description.contains("train") || description.contains("taxi")) return "Transport";
        if (description.contains("movie") || description.contains("game") || description.contains("netflix")) return "Entertainment";
        if (description.contains("bill") || description.contains("electric") || description.contains("water")) return "Utilities";
        if (description.contains("shirt") || description.contains("shoe") || description.contains("shop")) return "Shopping";
        return "Other";
    }

    private void addRecurringExpense(Scanner scanner) {
        System.out.println("\n  Add Recurring com.expensetracker.model.Expense");
        System.out.print("Description: ");
        String description = scanner.nextLine();

        System.out.print(" Amount: $");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        System.out.print(" Date (YYYY-MM-DD) or ENTER for today: ");
        String dateInput = scanner.nextLine();
        LocalDate date = dateInput.isEmpty() ? LocalDate.now() : LocalDate.parse(dateInput);

        System.out.print(" Category: ");
        String category = scanner.nextLine();

        if (ExpenseDAO.addExpense(currentUser.getUsername(), description, amount, category, date, true)) {
            System.out.println(" Recurring expense added!");
            loadUserExpenses();
        } else {
            showError("Failed to save recurring expense!");
        }
    }

    private void viewExpensesWithStyle() {
        loadUserExpenses();

        if (expenses.isEmpty()) {
            showEmptyState("No expenses yet! Start tracking to see your financial journey.");
            return;
        }

        System.out.println("\n  Your com.expensetracker.model.Expense Timeline");
        System.out.println("=".repeat(80));

        Map<LocalDate, List<Expense>> expensesByDate = expenses.stream()
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .collect(Collectors.groupingBy(Expense::getDate));

        for (Map.Entry<LocalDate, List<Expense>> entry : expensesByDate.entrySet()) {
            System.out.printf("\n %s\n", entry.getKey());
            System.out.println("-".repeat(40));

            double dayTotal = 0;
            for (Expense expense : entry.getValue()) {
                System.out.printf("   %-15s $%-8.2f %s\n",
                        expense.getCategory(), expense.getAmount(), expense.getDescription());
                dayTotal += expense.getAmount();
            }
            System.out.printf("    Daily Total: $%.2f\n", dayTotal);
        }

        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
        System.out.println("\n" + "=".repeat(40));
        System.out.printf(" GRAND TOTAL: $%.2f\n", total);
    }

    private void smartSearch(Scanner scanner) {
        System.out.println("\n  Smart Search Engine");
        System.out.println("1. Search by Description");
        System.out.println("2. Search by Category");
        System.out.println("3. Search by Amount Range");
        System.out.println("4. Search by Date Range");
        System.out.print("Choose: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        List<Expense> results = new ArrayList<>();

        switch (choice) {
            case 1:
                System.out.print("Enter keyword: ");
                String keyword = scanner.nextLine();
                results = expenses.stream()
                        .filter(e -> e.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                        .collect(Collectors.toList());
                break;

            case 2:
                System.out.print("Enter category: ");
                String category = scanner.nextLine();
                results = expenses.stream()
                        .filter(e -> e.getCategory().equalsIgnoreCase(category))
                        .collect(Collectors.toList());
                break;

            case 3:
                System.out.print("Min amount: $");
                double min = scanner.nextDouble();
                System.out.print("Max amount: $");
                double max = scanner.nextDouble();
                scanner.nextLine();
                results = expenses.stream()
                        .filter(e -> e.getAmount() >= min && e.getAmount() <= max)
                        .collect(Collectors.toList());
                break;

            case 4:
                System.out.print("Start date (YYYY-MM-DD): ");
                LocalDate start = LocalDate.parse(scanner.nextLine());
                System.out.print("End date (YYYY-MM-DD): ");
                LocalDate end = LocalDate.parse(scanner.nextLine());
                results = expenses.stream()
                        .filter(e -> !e.getDate().isBefore(start) && !e.getDate().isAfter(end))
                        .collect(Collectors.toList());
                break;
        }

        if (results.isEmpty()) {
            showEmptyState("No expenses found matching your criteria.");
        } else {
            System.out.println("\n Found " + results.size() + " expenses:");
            results.forEach(expense -> {
                System.out.printf("   %s - $%.2f - %s\n",
                        expense.getCategory(), expense.getAmount(), expense.getDescription());
            });
        }
    }

    private void showVisualAnalytics() {
        loadUserExpenses();

        if (expenses.isEmpty()) {
            showEmptyState("No data for analytics yet!");
            return;
        }

        System.out.println("\n  Financial Analytics Dashboard");
        System.out.println("=".repeat(60));

        Map<String, Double> categorySpending = expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)
                ));

        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();

        System.out.println("\n Category Breakdown:");
        System.out.println("-".repeat(40));

        categorySpending.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .forEach(entry -> {
                    double percentage = (entry.getValue() / total) * 100;
                    int bars = (int) (percentage / 2);
                    String bar = "█".repeat(bars) + "░".repeat(50 - bars);
                    System.out.printf("%-15s %s %5.1f%% $%.2f\n",
                            entry.getKey(), bar, percentage, entry.getValue());
                });
    }

    private void showSavingsProgress(Scanner scanner) {
        System.out.println("\n  Savings Progress");
        System.out.println("This feature works with savings goals set in Settings");
        System.out.println("Please set your savings goal in Settings menu first!");
    }

    private void loadUserExpenses() {
        if (currentUser != null) {
            expenses = ExpenseDAO.getUserExpenses(currentUser.getUsername());
        }
    }

    private double getCurrentMonthSpending() {
        if (currentUser != null) {
            return ExpenseDAO.getMonthlySpending(currentUser.getUsername());
        }
        return 0.0;
    }

    private void showError(String message) {
        System.out.println(" " + message);
    }

    private void showEmptyState(String message) {
        System.out.println("  " + message);
    }
}