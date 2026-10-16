package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;
import java.util.Scanner;

public class StaffMenu {
    private final UserDao userDao;
    private final Scanner scanner;

    public StaffMenu(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void display(User staff) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n========================================");
            System.out.println("      COMPUTER SHOP STAFF MENU          ");
            System.out.println("========================================");
            System.out.println("1. Process PC Rental / Time Session");
            System.out.println("2. Check PC Station Status");
            System.out.println("3. Add / Top-up Member Balance");
            System.out.println("4. Record Shift Sales & Cash Collection");
            System.out.println("5. View Daily Transaction History");
            System.out.println("0. Logout");
            System.out.println("----------------------------------------");
            System.out.print("Choose an option (0-5): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println("\nProcess PC Rental / Time Session");
                    break;
                case "2":
                    System.out.println("\nCheck PC Station Status");
                    break;
                case "3":
                    System.out.println("\nAdd / Top-up Member Balance");
                    break;
                case "4":
                    System.out.println("\nRecord Shift Sales & Cash Collection");
                    break;
                case "5":
                    System.out.println("\nView Daily Transaction History");
                    break;
                case "0":
                    System.out.println("\nYou have been logged out.");
                    loggedIn = false;
                    break;
                default:
                    System.out.println("\nInvalid choice. Please select an option between 0 and 5.");
            }
        }
    }
}