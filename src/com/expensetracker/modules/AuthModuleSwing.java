package com.expensetracker.modules;
import com.expensetracker.interfaces.IAuthOperations;
import com.expensetracker.dao.UserDAO;
import com.expensetracker.model.User;
import javax.swing.*;
import java.awt.*;
import java.util.function.BiConsumer;

public class AuthModuleSwing implements IAuthOperations {

    public void showLoginScreen(BiConsumer<String, Double> onSuccess) {
        JFrame loginFrame = new JFrame("Login");
        loginFrame.setSize(400, 300);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLayout(new BorderLayout(10, 10));
        loginFrame.setLocationRelativeTo(null);

        JLabel headerLabel = new JLabel("Login to Your Account", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();

        formPanel.add(userLabel);
        formPanel.add(userField);
        formPanel.add(passLabel);
        formPanel.add(passField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton loginBtn = new JButton("Login");
        JButton backBtn = new JButton("Back");

        loginBtn.setBackground(new Color(0, 123, 255));
        loginBtn.setForeground(Color.WHITE);
        backBtn.setBackground(new Color(108, 117, 125));
        backBtn.setForeground(Color.WHITE);

        buttonPanel.add(backBtn);
        buttonPanel.add(loginBtn);

        JLabel statusLabel = new JLabel("", JLabel.CENTER);

        loginBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Please fill all fields!");
                statusLabel.setForeground(Color.RED);
                return;
            }

            User user = UserDAO.authenticateUser(username, password);
            if (user != null) {
                statusLabel.setText("Login successful!");
                statusLabel.setForeground(Color.GREEN);

                Timer timer = new Timer(1000, ev -> {
                    loginFrame.dispose();
                    onSuccess.accept(username, user.getMonthlyIncome());
                });
                timer.setRepeats(false);
                timer.start();
            } else {
                statusLabel.setText("Invalid credentials!");
                statusLabel.setForeground(Color.RED);
            }
        });

        backBtn.addActionListener(e -> {
            loginFrame.dispose();
            showWelcomeScreen();
        });

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(buttonPanel, BorderLayout.CENTER);
        southPanel.add(statusLabel, BorderLayout.SOUTH);

        loginFrame.add(headerLabel, BorderLayout.NORTH);
        loginFrame.add(formPanel, BorderLayout.CENTER);
        loginFrame.add(southPanel, BorderLayout.SOUTH);
        loginFrame.setVisible(true);
    }

    public void showCreateAccountScreen(BiConsumer<String, Double> onSuccess) {
        JFrame createFrame = new JFrame("Create Account");
        createFrame.setSize(400, 350);
        createFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        createFrame.setLayout(new BorderLayout(10, 10));
        createFrame.setLocationRelativeTo(null);

        JLabel headerLabel = new JLabel("Create New Account", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();
        JLabel incomeLabel = new JLabel("Monthly Income:");
        JTextField incomeField = new JTextField();

        formPanel.add(userLabel);
        formPanel.add(userField);
        formPanel.add(passLabel);
        formPanel.add(passField);
        formPanel.add(incomeLabel);
        formPanel.add(incomeField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton createBtn = new JButton("Create Account");
        JButton backBtn = new JButton("Back");

        createBtn.setBackground(new Color(40, 167, 69));
        createBtn.setForeground(Color.WHITE);
        backBtn.setBackground(new Color(108, 117, 125));
        backBtn.setForeground(Color.WHITE);

        buttonPanel.add(backBtn);
        buttonPanel.add(createBtn);

        JLabel statusLabel = new JLabel("", JLabel.CENTER);

        createBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            String incomeText = incomeField.getText();

            if (username.isEmpty() || password.isEmpty() || incomeText.isEmpty()) {
                statusLabel.setText("Please fill all fields!");
                statusLabel.setForeground(Color.RED);
                return;
            }

            try {
                double income = Double.parseDouble(incomeText);

                if (UserDAO.createUser(username, password, income)) {
                    statusLabel.setText("Account created!");
                    statusLabel.setForeground(Color.GREEN);

                    Timer timer = new Timer(1000, ev -> {
                        createFrame.dispose();
                        onSuccess.accept(username, income);
                    });
                    timer.setRepeats(false);
                    timer.start();
                } else {
                    statusLabel.setText("Username already exists!");
                    statusLabel.setForeground(Color.RED);
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("Invalid income!");
                statusLabel.setForeground(Color.RED);
            }
        });

        backBtn.addActionListener(e -> {
            createFrame.dispose();
            showWelcomeScreen();
        });

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(buttonPanel, BorderLayout.CENTER);
        southPanel.add(statusLabel, BorderLayout.SOUTH);

        createFrame.add(headerLabel, BorderLayout.NORTH);
        createFrame.add(formPanel, BorderLayout.CENTER);
        createFrame.add(southPanel, BorderLayout.SOUTH);
        createFrame.setVisible(true);
    }

    private void showWelcomeScreen() {
        // This would redirect to com.expensetracker.main.MainSwing welcome screen
        // In a real app, you'd have a navigation controller
        JOptionPane.showMessageDialog(null, "Please restart the application");
        System.exit(0);
    }
}