package com.scc.ui;

import com.scc.dao.RentalDao;
import com.scc.dao.UserDao;
import com.scc.model.User;
import java.util.Scanner;

public class StaffMenu {
    private final UserDao userDao;
    private final RentalDao rentalDao;
    private final Scanner scanner;

    public StaffMenu(UserDao userDao, RentalDao rentalDao, Scanner scanner) {
        this.userDao = userDao;
        this.rentalDao = rentalDao;
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
            System.out.println("6. Update Rental Status");
            System.out.println("0. Logout");
            System.out.println("----------------------------------------");
            System.out.print("Choose an option (0-6): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleProcessRental(staff);
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
                case "6":
                    handleUpdateRentalStatus(staff);
                    break;
                case "0":
                    System.out.println("\nYou have been logged out.");
                    loggedIn = false;
                    break;
                default:
                    System.out.println("\nInvalid choice. Please select an option between 0 and 6.");
            }
        }
    }

    private void handleProcessRental(User staff) {
        System.out.println("\n--- PROCESS PC RENTAL ---");
        System.out.print("Customer name: ");
        String customer = scanner.nextLine().trim();
        System.out.print("PC station (e.g. PC-01): ");
        String pc = scanner.nextLine().trim();
        if (customer.isEmpty() || pc.isEmpty()) {
            System.out.println("Customer name and PC station are required.");
            return;
        }

        System.out.print("Minutes rented: ");
        int minutes;
        try {
            minutes = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a whole number of minutes.");
            return;
        }
        if (minutes <= 0) {
            System.out.println("Minutes must be greater than 0.");
            return;
        }

        int rentalId = rentalDao.processRental(staff.id(), customer, pc, minutes);
        if (rentalId > 0) {
            System.out.printf("Rental #%d recorded. Total: P%.2f%n",
                    rentalId, RentalDao.RATE_PER_HOUR * minutes / 60.0);
        } else {
            System.out.println("Failed to process rental.");
        }
    }

    private void handleUpdateRentalStatus(User staff) {
        System.out.println("\n--- UPDATE RENTAL STATUS ---");
        System.out.print("Rental ID: ");
        int rentalId;
        try {
            rentalId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a numeric Rental ID.");
            return;
        }

        System.out.print("New status [1] ACTIVE [2] COMPLETED [3] VOIDED: ");
        String status = switch (scanner.nextLine().trim()) {
            case "1" -> "ACTIVE";
            case "2" -> "COMPLETED";
            case "3" -> "VOIDED";
            default -> null;
        };
        if (status == null) {
            System.out.println("Invalid status selection.");
            return;
        }

        if (rentalDao.updateRentalStatus(staff.id(), rentalId, status)) {
            System.out.println("Rental #" + rentalId + " status set to " + status + ".");
        } else {
            System.out.println("Rental not found or update failed.");
        }
    }
}