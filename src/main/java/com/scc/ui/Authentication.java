package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.dao.DatabaseInitializer;
import com.scc.dao.RentalDao;
import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.Scanner;

public class Authentication {
    private static final Scanner sc = new Scanner(System.in);
    private static UserDao userDao;
    private static ActivityLogDao activityLogDao;
    private static RentalDao rentalDao;

    public static void main(String[] args) {
        // Tables must exist BEFORE the DAOs run (UserDao seeds the default admin).
        DatabaseInitializer.initializeDatabase();
        userDao = new UserDao();
        activityLogDao = new ActivityLogDao();
        rentalDao = new RentalDao();

        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("========================================");
            System.out.println("      COMPUTER SHOP BILLING SYSTEM      ");
            System.out.println("========================================");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    handleRegister();
                    break;
                case 3:
                    System.out.println("Exiting the system. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        sc.close();
    }

    private static void handleRegister() {
        System.out.print("Enter a new username: ");
        String user = sc.nextLine().trim();
        System.out.print("Enter a new password: ");
        String pass = sc.nextLine().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            System.out.println("Username and password are required.");
            return;
        }

        System.out.print("Select role [1] STAFF [2] USER: ");
        String role = switch (sc.nextLine().trim()) {
            case "1" -> "STAFF";
            case "2" -> "USER";
            default -> null;
        };

        if (role == null) {
            System.out.println("Invalid role selection. Please try again.");
            return;
        }

        if (userDao.registerUser(user, pass, role, "PENDING")) {
            System.out.println("Registration submitted successfully! Waiting for admin approval.");
        } else {
            System.out.println("Registration failed. That username may already be taken.");
        }
    }

    private static void handleLogin() {
        System.out.print("Enter username: ");
        String username = sc.nextLine().trim();
        System.out.print("Enter password: ");
        String password = sc.nextLine().trim();

        User user = userDao.login(username, password);

        if (user != null) {
            System.out.println("Login successful! Welcome, " + user.username() + " | ROLE: " + user.roles());
            activityLogDao.logAction(user.id(), "LOGIN",
                    "User " + user.username() + " (ID: " + user.id() + ") logged in.");

            switch (user.roles().toUpperCase()) {
                case "ADMIN" -> new AdminMenu(userDao, activityLogDao, sc).display(user);
                case "STAFF" -> new StaffMenu(userDao, rentalDao, sc).display(user);
                case "USER" -> new UserMenu(userDao, sc).display(user);
                default -> System.out.println("Unknown role. Access denied.");
            }

            activityLogDao.logAction(user.id(), "LOGOUT",
                    "User " + user.username() + " (ID: " + user.id() + ") logged out.");
        } else {
            System.out.println("Login failed. Invalid credentials, or the account is not approved.");
        }
    }
}