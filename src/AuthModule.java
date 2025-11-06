import com.expensetracker.dao.UserDAO;
import com.expensetracker.model.User;

import java.util.Scanner;

public class AuthModule {

    public User createAccount(Scanner scanner) {
        System.out.println("\n Let's create your Personal com.expensetracker.model.Expense Tracker!");
        System.out.print(" Username: ");
        String username = scanner.nextLine();

        if (username.length() < 3) {
            showError("Username must be at least 3 characters!");
            return null;
        }

        if (UserDAO.usernameExists(username)) {
            showError("Username already exists! Choose another.");
            return null;
        }

        System.out.print(" Password: ");
        String password = scanner.nextLine();

        System.out.print(" Monthly Income: ");
        double income = scanner.nextDouble();
        scanner.nextLine();

        if (UserDAO.createUser(username, password, income)) {
            User user = new User(username, password, income);
            showMotivationalQuote();
            System.out.println("  Account created! Welcome to Personal com.expensetracker.model.Expense Tracker, " + username + "!");
            return user;
        } else {
            showError("Failed to create account! Please try again.");
            return null;
        }
    }

    public User login(Scanner scanner) {
        System.out.println("\n Welcome Back!");
        System.out.print(" Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        User user = UserDAO.authenticateUser(username, password);
        if (user != null) {
            showMotivationalQuote();
            System.out.println(" Login successful! Welcome back, " + username + "!");
            return user;
        } else {
            showError("Invalid credentials!");
            return null;
        }
    }

    private void showMotivationalQuote() {
        String[] quotes = {
                "Small savings today create big fortunes tomorrow!",
                "Tracking expenses is the first step to financial freedom!",
                "Every dollar counted is a dollar saved!"
        };
        System.out.println("\n  " + quotes[new java.util.Random().nextInt(quotes.length)]);
    }

    private void showError(String message) {
        System.out.println(" ❌ " + message);
    }
}