import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.model.Expense;
import com.expensetracker.model.User;

import java.util.*;
import java.io.*;

public class SettingsModule {
    private User currentUser;
    private Double overallBudget = null;
    private Map<String, Double> categoryBudgets = new HashMap<>();
    private double savingsGoal = 0.0;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void handleSettingsOperations(int choice, Scanner scanner) {
        switch (choice) {
            case 7: setBudgetsAndGoals(scanner); break;
            case 8: exportData(scanner); break;
            case 9:
                System.out.println("\n Thank you for using Personal com.expensetracker.model.Expense Tracker!");
                break;
        }
    }

    private void setBudgetsAndGoals(Scanner scanner) {
        System.out.println("\n  Financial Goals & Budgets");
        System.out.println("1. Set Monthly Budget");
        System.out.println("2. Set Category Budgets");
        System.out.println("3. Set Savings Goal");
        System.out.println("4. View Current Settings");
        System.out.print("Choose: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                System.out.print("Enter overall monthly budget: $");
                overallBudget = scanner.nextDouble();
                scanner.nextLine();
                System.out.println("  Monthly budget set to $" + overallBudget);
                break;

            case 2:
                System.out.print("Enter category: ");
                String category = scanner.nextLine();
                System.out.print("Enter budget: $");
                double budget = scanner.nextDouble();
                scanner.nextLine();
                categoryBudgets.put(category, budget);
                System.out.println("  " + category + " budget set to $" + budget);
                break;

            case 3:
                setSavingsGoal(scanner);
                break;

            case 4:
                System.out.println("\nCurrent Financial Settings:");
                System.out.printf("   Monthly Budget: $%.2f\n", overallBudget != null ? overallBudget : 0.0);
                System.out.println("   Category Budgets: " + categoryBudgets);
                System.out.printf("   Savings Goal: $%.2f\n", savingsGoal);
                break;
        }
    }

    private void setSavingsGoal(Scanner scanner) {
        System.out.print("Enter monthly savings goal: $");
        savingsGoal = scanner.nextDouble();
        scanner.nextLine();
        System.out.println("  Savings goal set to $" + savingsGoal);
        System.out.println("  You can do it! Every dollar counts!");
    }

    private void exportData(Scanner scanner) {
        System.out.println("\n  Data Export");
        System.out.print("Enter filename (e.g., my_finances.csv): ");
        String filename = scanner.nextLine();

        // Get expenses for current user
        List<Expense> expenses = ExpenseDAO.getUserExpenses(currentUser.getUsername());

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Date,Category,Amount,Description");
            for (Expense expense : expenses) {
                writer.printf("%s,%s,%.2f,%s%n",
                        expense.getDate(), expense.getCategory(), expense.getAmount(),
                        expense.getDescription().replace(",", ";"));
            }
            System.out.println("  Data exported to " + filename);
        } catch (IOException e) {
            System.out.println("  Export failed: " + e.getMessage());
        }
    }
}