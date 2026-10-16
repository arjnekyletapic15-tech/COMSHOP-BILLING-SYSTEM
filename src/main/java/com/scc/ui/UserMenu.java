package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.Scanner;

public class UserMenu {
    private final UserDao userDao;
    private final Scanner sc;

    public UserMenu(UserDao userDao, Scanner sc) {
        this.userDao = userDao;
        this.sc = sc;
    }

    public void display(User user) {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("========================================");
            System.out.println("COMPUTER SHOP TIME & BILLING - SUPERVISOR");
            System.out.println("========================================");
            System.out.println(" Logged in as: " + user.username());
            System.out.println("----------------------------------------");
            System.out.println(" [1] Approve Timer Override / Extension / Refund");
            System.out.println(" [2] View Shift Cash Totals");
            System.out.println(" [3] Generate Shift Report");
            System.out.println(" [4] Handle Customer Time Disputes");
            System.out.println(" [0] Logout");
            System.out.println("----------------------------------------");
            System.out.print("Choose an option: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    approveOverride();
                    break;
                case "2":
                    viewShiftCashTotals();
                    break;
                case "3":
                    generateShiftReport();
                    break;
                case "4":
                    handleDisputes();
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

    private void approveOverride() {
        System.out.println("\n--- APPROVE OVERRIDE / EXTENSION / REFUND ---");
        System.out.println("Review and approve manual timer changes or refunds.");
    }

    private void viewShiftCashTotals() {
        System.out.println("\n--- SHIFT CASH TOTALS ---");
        System.out.println("Total cash collected per cashier for the current shift.");

    }

    private void generateShiftReport() {
        System.out.println("\n--- SHIFT REPORT ---");
        System.out.println("Cash, minutes sold, and transaction count for the shift.");

    }

    private void handleDisputes() {
        System.out.println("\n--- CUSTOMER TIME DISPUTES ---");
        System.out.println("Look up a session and resolve an escalated complaint.");

    }
}