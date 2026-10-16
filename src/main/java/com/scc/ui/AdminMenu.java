package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final UserDao userDao;
    private final Scanner scanner;

    public AdminMenu(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void display(User admin) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n=====================================");
            System.out.println("        ADMIN DASHBOARD (COMSHOP)    ");
            System.out.println("=====================================");
            System.out.println("1. View All Users");
            System.out.println("2. View Pending User Approvals");
            System.out.println("3. Approve User Registration");
            System.out.println("4. Add New User");
            System.out.println("5. Update User Role / Status");
            System.out.println("6. Delete User Account");
            System.out.println("7. Logout");
            System.out.print("Choose an option (1-7): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleViewAllUsers();
                    break;
                case "2":
                    handleViewPendingUsers();
                    break;
                case "3":
                    handleApproveUser();
                    break;
                case "4":
                    System.out.println("\nAdd New User feature.");
                    break;
                case "5":
                    System.out.println("\nUpdate User Role / Status feature.");
                    break;
                case "6":
                    System.out.println("\nDelete User Account feature.");
                    break;
                case "7":
                    System.out.println("\nYou have been logged out.");
                    loggedIn = false;
                    break;
                default:
                    System.out.println("\nInvalid choice. Please choose between 1 and 7.");
            }
        }
    }

    private void printUserTable(List<User> users) {
        String format = "%-5s | %-15s | %-12s | %-10s%n";
        String border = "--------------------------------------------------";

        System.out.println(border);
        System.out.printf(format, "ID", "Username", "Role", "Status");
        System.out.println(border);

        for (User u : users) {
            System.out.printf(format, u.id(), u.username(), u.roles(), u.status());
        }
        System.out.println(border);
    }

    private void handleViewAllUsers() {
        List<User> allUsers = userDao.getAllUsers();
        if (allUsers.isEmpty()) {
            System.out.println("No users found in the system.");
            return;
        }
        System.out.println("\n--- ALL SYSTEM USERS ---");
        printUserTable(allUsers);
    }

    private void handleViewPendingUsers() {
        List<User> pendingList = userDao.getPendingUsers();
        if (pendingList.isEmpty()) {
            System.out.println("No pending user accounts found.");
            return;
        }
        System.out.println("\n--- PENDING USER ACCOUNTS ---");
        printUserTable(pendingList);
    }

    private void handleApproveUser() {
        List<User> pendingList = userDao.getPendingUsers();
        if (pendingList.isEmpty()) {
            System.out.println("No pending user accounts to approve.");
            return;
        }

        System.out.println("\n--- PENDING USER ACCOUNTS ---");
        printUserTable(pendingList);

        System.out.print("Enter User ID to approve (or 0 to cancel): ");
        try {
            int userId = Integer.parseInt(scanner.nextLine().trim());
            if (userId == 0) {
                System.out.println("Approval action cancelled.");
                return;
            }

            boolean exists = pendingList.stream().anyMatch(u -> u.id() == userId);
            if (!exists) {
                System.out.println("Error: User ID not found in the pending list.");
                return;
            }

            boolean success = userDao.updateUserStatus(userId, "APPROVED");
            if (success) {
                System.out.println("User account with ID " + userId + " successfully APPROVED!");
            } else {
                System.out.println("Failed to update user status.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a numeric User ID.");
        }
    }
}