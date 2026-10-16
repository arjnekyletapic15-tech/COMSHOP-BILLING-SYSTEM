package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;
import java.util.Scanner;

public class UserMenu {
    private final UserDao userDao;
    private final Scanner scanner;

    public UserMenu(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void display(User user) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n========================================");
            System.out.println("      COMPUTER SHOP CUSTOMER MENU       ");
            System.out.println("========================================");
            System.out.println("1. View PC Rental Rates & Promos");
            System.out.println("2. View Remaining Account Time / Balance");
            System.out.println("3. View Rental / Session History");
            System.out.println("4. Update Account Details");
            System.out.println("0. Logout");
            System.out.println("----------------------------------------");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println("\nView PC Rental Rates & Promos");
                    break;
                case "2":
                    System.out.println("\nView Remaining Account Time / Balance");
                    break;
                case "3":
                    System.out.println("\nView Rental / Session History");
                    break;
                case "4":
                    System.out.println("\nUpdate Account Details");
                    break;
                case "0":
                    System.out.println("\nYou have been logged out.");
                    loggedIn = false;
                    break;
                default:
                    System.out.println("\nInvalid choice. Please try again.");
            }
        }
    }
}