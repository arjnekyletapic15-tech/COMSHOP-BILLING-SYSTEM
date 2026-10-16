package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.Scanner;

public class OtherRoleMenu {
    private final UserDao userDao;
    private final Scanner scanner;
    private final User currentUser;

    public OtherRoleMenu(UserDao userDao, Scanner scanner, User currentUser) {
        this.userDao = userDao;
        this.scanner = scanner;
        this.currentUser = currentUser;
    }

    public void display() {
        boolean active = true;
        while (active) {
            System.out.println("\n=== " + currentUser.roles() + " DASHBOARD (" + currentUser.username() + ") ===");
            System.out.println("1. View Profile & Membership Info");
            System.out.println("2. Logout");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    System.out.println("Username: " + currentUser.username());
                    System.out.println("Assigned Role: " + currentUser.roles());
                    System.out.println("Account Status: " + currentUser.status());
                    break;
                case "2":
                    System.out.println("Logging out...");
                    active = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}