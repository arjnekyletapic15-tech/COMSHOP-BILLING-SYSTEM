package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.dao.UserDao;
import com.scc.model.ActivityLog;
import com.scc.model.User;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private static final int DESC_WIDTH = 35;

    private final UserDao userDao;
    private final ActivityLogDao activityLogDao;
    private final Scanner scanner;

    public AdminMenu(UserDao userDao, ActivityLogDao activityLogDao, Scanner scanner) {
        this.userDao = userDao;
        this.activityLogDao = activityLogDao;
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
            System.out.println("7. View System Audit Logs");
            System.out.println("8. Logout");
            System.out.print("Choose an option (1-8): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleViewAllUsers();
                    break;
                case "2":
                    handleViewPendingUsers();
                    break;
                case "3":
                    handleApproveUser(admin);
                    break;
                case "4":
                    System.out.println("\nAdd New User feature.");
                    break;
                case "5":
                    handleUpdateRoleAndStatus(admin);
                    break;
                case "6":
                    System.out.println("\nDelete User Account feature.");
                    break;
                case "7":
                    handleAuditLogs();
                    break;
                case "8":
                    System.out.println("\nYou have been logged out.");
                    loggedIn = false;
                    break;
                default:
                    System.out.println("\nInvalid choice. Please choose between 1 and 8.");
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

    private void handleApproveUser(User admin) {
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

            boolean success = userDao.approveUser(admin.id(), userId);
            if (success) {
                System.out.println("User account with ID " + userId + " successfully APPROVED!");
            } else {
                System.out.println("Failed to update user status.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a numeric User ID.");
        }
    }

    private void handleUpdateRoleAndStatus(User admin) {
        List<User> allUsers = userDao.getAllUsers();
        if (allUsers.isEmpty()) {
            System.out.println("No users found in the system.");
            return;
        }

        System.out.println("\n--- UPDATE USER ROLE / STATUS ---");
        printUserTable(allUsers);

        System.out.print("Enter User ID to update (or 0 to cancel): ");
        int userId;
        try {
            userId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a numeric User ID.");
            return;
        }
        if (userId == 0) {
            System.out.println("Update cancelled.");
            return;
        }
        if (allUsers.stream().noneMatch(u -> u.id() == userId)) {
            System.out.println("Error: User ID not found.");
            return;
        }

        System.out.print("New role [1] ADMIN [2] STAFF [3] USER: ");
        String role = switch (scanner.nextLine().trim()) {
            case "1" -> "ADMIN";
            case "2" -> "STAFF";
            case "3" -> "USER";
            default -> null;
        };
        if (role == null) {
            System.out.println("Invalid role selection. Update cancelled.");
            return;
        }

        System.out.print("New status [1] APPROVED [2] PENDING [3] REJECTED: ");
        String status = switch (scanner.nextLine().trim()) {
            case "1" -> "APPROVED";
            case "2" -> "PENDING";
            case "3" -> "REJECTED";
            default -> null;
        };
        if (status == null) {
            System.out.println("Invalid status selection. Update cancelled.");
            return;
        }

        if (userDao.updateUserRoleAndStatus(admin.id(), userId, role, status)) {
            System.out.println("User " + userId + " updated: role=" + role + ", status=" + status);
        } else {
            System.out.println("Failed to update user.");
        }
    }

    // ---------- Audit log screens ----------

    private void handleAuditLogs() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- SYSTEM AUDIT LOGS ---");
            System.out.println("1. View All System Logs");
            System.out.println("2. Filter Logs by User ID");
            System.out.println("0. Back");
            System.out.print("Choose an option: ");

            switch (scanner.nextLine().trim()) {
                case "1":
                    printLogTable(activityLogDao.getAllLogs());
                    break;
                case "2":
                    handleFilterLogsByUser();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please choose 0, 1 or 2.");
            }
        }
    }

    private void handleFilterLogsByUser() {
        while (true) {
            System.out.print("Enter User ID to filter (or 0 to cancel): ");
            try {
                int userId = Integer.parseInt(scanner.nextLine().trim());
                if (userId == 0) {
                    return;
                }
                printLogTable(activityLogDao.getLogsByUserId(userId));
                return;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric User ID.");
            }
        }
    }

    private void printLogTable(List<ActivityLog> logs) {
        if (logs.isEmpty()) {
            System.out.println("No audit log entries found.");
            return;
        }

        String format = "%-5s | %-8s | %-18s | %-35s | %-20s%n";
        String border = "-".repeat(5 + 3 + 8 + 3 + 18 + 3 + DESC_WIDTH + 3 + 20);

        System.out.println(border);
        System.out.printf(format, "ID", "User ID", "Action Type", "Description", "Timestamp");
        System.out.println(border);

        for (ActivityLog log : logs) {
            List<String> lines = wrap(log.description(), DESC_WIDTH);
            System.out.printf(format, log.id(), log.userId(), log.actionType(), lines.get(0), log.timestamp());
            for (int i = 1; i < lines.size(); i++) {
                System.out.printf(format, "", "", "", lines.get(i), "");
            }
        }
        System.out.println(border);
        System.out.println(logs.size() + " log entr" + (logs.size() == 1 ? "y" : "ies") + " shown.");
    }

    /** Word-wraps text so long descriptions keep the table aligned without losing content. */
    private List<String> wrap(String text, int width) {
        List<String> lines = new java.util.ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String word : text.split(" ")) {
            while (word.length() > width) {
                if (current.length() > 0) {
                    lines.add(current.toString());
                    current.setLength(0);
                }
                lines.add(word.substring(0, width));
                word = word.substring(width);
            }
            if (current.length() == 0) {
                current.append(word);
            } else if (current.length() + 1 + word.length() <= width) {
                current.append(' ').append(word);
            } else {
                lines.add(current.toString());
                current = new StringBuilder(word);
            }
        }
        if (current.length() > 0 || lines.isEmpty()) {
            lines.add(current.toString());
        }
        return lines;
    }
}