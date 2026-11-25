package com.scc.util;

import java.util.Scanner;

public final class InputUtil {

    private InputUtil() {
    }

    /** Re-prompts until the user enters a whole number. */
    public static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }

    /** Re-prompts until the user enters a whole number greater than 0. */
    public static int readPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            int value = readInt(scanner, prompt);
            if (value > 0) {
                return value;
            }
            System.out.println("Value must be greater than 0.");
        }
    }

    /** Re-prompts until the user enters non-empty text. */
    public static String readNonEmpty(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("This field cannot be empty.");
        }
    }
}