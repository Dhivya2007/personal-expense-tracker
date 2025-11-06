import com.expensetracker.dao.DatabaseManager;
import com.expensetracker.model.User;

import java.util.*;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;

    // Modules
    private static AuthModule authModule = new AuthModule();
    private static ExpenseModule expenseModule = new ExpenseModule();
    private static SettingsModule settingsModule = new SettingsModule();

    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();
        cleanupWrongData();
        showWelcomeAnimation();

        while (true) {
            if (currentUser == null) {
                showAuthMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private static void showWelcomeAnimation() {
        System.out.println("\n\n");
        String[] frames = {
                " PERSONAL EXPENSE TRACKER",
                " PERSONAL EXPENSE TRACKER ",
                " PERSONAL EXPENSE TRACKER ",
                " PERSONAL EXPENSE TRACKER "
        };

        try {
            for (String frame : frames) {
                System.out.print("\r" + frame);
                Thread.sleep(300);
            }
        } catch (InterruptedException e) {}

        System.out.println("\n\n Your Ultimate Financial Companion ");
        System.out.println("===========================================");
    }

    private static void showAuthMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           ACCOUNT PORTAL");
        System.out.println("=".repeat(50));
        System.out.println("1. Create Account");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Choose: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    currentUser = authModule.createAccount(scanner);
                    break;
                case 2:
                    currentUser = authModule.login(scanner);
                    break;
                case 3:
                    exitApp();
                    break;
                default:
                    showError("Invalid choice! Try again.");
            }
        } catch (InputMismatchException e) {
            showError("Please enter a number!");
            scanner.nextLine();
        }
    }

    private static void showMainMenu() {
        while (currentUser != null) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("    Personal com.expensetracker.model.Expense Tracker - Control Panel");
            System.out.println("=".repeat(50));

            // Set current user for all modules
            expenseModule.setCurrentUser(currentUser);
            settingsModule.setCurrentUser(currentUser);

            // Display financial overview
            expenseModule.displayFinancialOverview();

            System.out.println("\n EXPENSE MANAGEMENT");
            System.out.println("1. Add com.expensetracker.model.Expense");
            System.out.println("2. Add Recurring com.expensetracker.model.Expense");
            System.out.println("3. View All Expenses");
            System.out.println("4. Smart Search & Filters");

            System.out.println("\n FINANCIAL INSIGHTS");
            System.out.println("5. Visual Analytics");
            System.out.println("6. Savings Progress");

            System.out.println("\n  SETTINGS & TOOLS");
            System.out.println("7. Set Budgets & Goals");
            System.out.println("8. Export Data");
            System.out.println("9. Logout");

            System.out.print("\n Choose: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                        expenseModule.handleExpenseOperations(choice, scanner);
                        break;
                    case 5:
                    case 6:
                        expenseModule.handleFinancialInsights(choice, scanner);
                        break;
                    case 7:
                    case 8:
                    case 9:
                        settingsModule.handleSettingsOperations(choice, scanner);
                        if (choice == 9) {
                            logout();
                        }
                        break;
                    default:
                        showError("Invalid choice!");
                }
            } catch (InputMismatchException e) {
                showError("Please enter a number!");
                scanner.nextLine();
            }
        }
    }

    private static void logout() {
        System.out.println("\n Thank you for using Personal com.expensetracker.model.Expense Tracker!");
        currentUser = null;
        expenseModule.setCurrentUser(null);
        settingsModule.setCurrentUser(null);
    }

    private static void exitApp() {
        System.out.println("\n Thanks for using Personal com.expensetracker.model.Expense Tracker!");
        System.out.println(" Your financial future looks bright!");
        System.exit(0);
    }

    private static void showError(String message) {
        System.out.println(" ❌ " + message);
    }

    private static void cleanupWrongData() {
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.createStatement()) {

            System.out.println(" Cleaning up wrong timestamp data...");
            String deleteSql = "DELETE FROM expenses WHERE expense_date > 1000000000000";
            int deletedCount = stmt.executeUpdate(deleteSql);
            System.out.println(" Deleted " + deletedCount + " expenses with wrong timestamps");

        } catch (Exception e) {
            System.err.println("Cleanup error: " + e.getMessage());
        }
    }
}