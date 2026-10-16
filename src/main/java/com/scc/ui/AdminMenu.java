package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.Scanner;

public class AdminMenu {
    private final UserDao userDao;
    private final Scanner sc;

    public AdminMenu(UserDao userDao, Scanner sc) {
        this.userDao = userDao;
        this.sc = sc;
    }

    public void display(User user) {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("========================================");
            System.out.println("  COMPUTER SHOP TIME & BILLING - ADMIN  ");
            System.out.println("========================================");
            System.out.println(" Logged in as: " + user.username());
            System.out.println("----------------------------------------");
            System.out.println(" [1] Manage PC Units");
            System.out.println(" [2] Manage Users & Roles");
            System.out.println(" [3] Manage Time Rates");
            System.out.println(" [4] View Time Transactions");
            System.out.println(" [5] View Daily Cash Collection Report");
            System.out.println(" [6] View Activity & Audit Logs");
            System.out.println(" [0] Logout");
            System.out.println("----------------------------------------");
            System.out.print("Choose an option: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    managePcUnits();
                    break;
                case "2":
                    manageUsersAndRoles();
                    break;
                case "3":
                    manageTimeRates();
                    break;
                case "4":
                    viewTimeTransactions();
                    break;
                case "5":
                    viewDailyCashReport();
                    break;
                case "6":
                    viewAuditLogs();
                    break;
                case "0":
                    System.out.println("You have been logged out.");
                    running = false; // goes back to the main menu
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void managePcUnits() {
        System.out.println("\n--- MANAGE PC UNITS ---");
        System.out.println("Add, view, update status, or remove PC units.");
    }

    private void manageUsersAndRoles() {
        System.out.println("\n--- MANAGE USERS & ROLES ---");
        System.out.println("Approve pending accounts, change roles, deactivate users.");
    }

    private void manageTimeRates() {
        System.out.println("\n--- MANAGE TIME RATES ---");
        System.out.println("Set rental rates (e.g., 60 mins = P17.00).");
    }

    private void viewTimeTransactions() {
        System.out.println("\n--- TIME TRANSACTIONS ---");
        System.out.println("View PC, cashier, amount paid, start and end time.");
    }

    private void viewDailyCashReport() {
        System.out.println("\n--- DAILY CASH COLLECTION REPORT ---");
        System.out.println("Total cash, minutes sold, and number of transactions.");
    }

    private void viewAuditLogs() {
        System.out.println("\n--- ACTIVITY & AUDIT LOGS ---");
        System.out.println("Logins, session starts and top-ups, rate changes.");
    }
}