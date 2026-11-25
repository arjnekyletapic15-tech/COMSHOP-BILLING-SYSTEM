package com.scc.ui;

import com.scc.dao.RentalDao;
import com.scc.dao.UserDao;
import com.scc.model.Rental;
import com.scc.model.User;
import com.scc.util.InputUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class StaffMenu {
    private static final int TOTAL_STATIONS = 5;

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
            System.out.println("5. View Rental Transaction History");
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
                    handleCheckPcStatus();
                    break;
                case "3":
                    System.out.println("\nAdd / Top-up Member Balance");
                    break;
                case "4":
                    System.out.println("\nRecord Shift Sales & Cash Collection");
                    break;
                case "5":
                    handleViewRentals();
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

    private String normalizeStation(String rawStation) {
        if (rawStation == null) {
            return "";
        }
        String cleaned = rawStation.trim().toUpperCase();
        if (cleaned.matches("\\d+")) {
            return String.format("PC-%02d", Integer.parseInt(cleaned));
        }
        if (cleaned.matches("PC-\\d+")) {
            int num = Integer.parseInt(cleaned.substring(3));
            return String.format("PC-%02d", num);
        }
        return cleaned;
    }

    private void handleProcessRental(User staff) {
        System.out.println("\n--- PROCESS PC RENTAL ---");
        String customer = InputUtil.readNonEmpty(scanner, "Customer name: ");
        String pc = normalizeStation(InputUtil.readNonEmpty(scanner, "PC station (e.g. PC-01 or 1): "));
        int minutes = InputUtil.readPositiveInt(scanner, "Minutes rented: ");

        int rentalId = rentalDao.processRental(staff.id(), customer, pc, minutes);
        if (rentalId > 0) {
            System.out.printf("Rental #%d recorded. Total: P%.2f%n",
                    rentalId, RentalDao.RATE_PER_HOUR * minutes / 60.0);
        } else {
            System.out.println("Failed to process rental.");
        }
    }

    private void handleCheckPcStatus() {
        List<Rental> rentals = rentalDao.getAllRentals();
        Map<String, Rental> activeByPc = new HashMap<>();

        for (Rental r : rentals) {
            if ("ACTIVE".equalsIgnoreCase(r.status()) && r.pcStation() != null) {
                String normalizedPc = normalizeStation(r.pcStation());
                activeByPc.putIfAbsent(normalizedPc, r);
            }
        }

        String border = "+----+---------+--------------------+------------+-----------+";
        String header = "| %-2s | %-7s | %-18s | %-10s | %-9s |%n";
        String row    = "| %2d | %-7s | %-18s | %-10s | %-9s |%n";

        System.out.println("\n--- PC STATION STATUS ---");
        System.out.println(border);
        System.out.printf(header, "ID", "STATION", "CUSTOMER", "MINUTES", "STATUS");
        System.out.println(border);

        for (int i = 1; i <= TOTAL_STATIONS; i++) {
            String pcCode = String.format("PC-%02d", i);
            Rental active = activeByPc.get(pcCode);

            if (active != null) {
                System.out.printf(row,
                        i,
                        pcCode,
                        active.customerName(),
                        active.minutes() + " mins",
                        "IN USE");
            } else {
                System.out.printf(row,
                        i,
                        pcCode,
                        "-",
                        "0 mins",
                        "AVAILABLE");
            }
        }
        System.out.println(border);
    }

    private void handleViewRentals() {
        List<Rental> rentals = rentalDao.getAllRentals();
        if (rentals.isEmpty()) {
            System.out.println("No rentals recorded yet.");
            return;
        }

        String header = "%-5s | %-18s | %-8s | %-7s | %-10s | %-10s | %-19s%n";
        String row = "%-5d | %-18s | %-8s | %-7d | P%-9.2f | %-10s | %-19s%n";
        String border = "-".repeat(95);

        System.out.println("\n--- RENTAL TRANSACTIONS ---");
        System.out.println(border);
        System.out.printf(header, "ID", "Customer", "PC", "Minutes", "Total", "Status", "Created");
        System.out.println(border);
        for (Rental r : rentals) {
            System.out.printf(row, r.id(), r.customerName(), r.pcStation(),
                    r.minutes(), r.totalAmount(), r.status(), r.createdAt());
        }
        System.out.println(border);
        System.out.println(rentals.size() + " rental" + (rentals.size() == 1 ? "" : "s") + " shown.");
    }

    private void handleUpdateRentalStatus(User staff) {
        System.out.println("\n--- UPDATE RENTAL STATUS ---");
        int rentalId = InputUtil.readInt(scanner, "Rental ID: ");

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