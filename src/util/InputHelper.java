package util;

import java.util.Scanner;

/**
 * Robust terminal input utility that prevents runtime crashes due to input mismatches.
 * Encapsulates Scanner handling and input validation with graceful EOF handling.
 */
public class InputHelper {
    private static final Scanner SCANNER = new Scanner(System.in);

    private InputHelper() { }

    /**
     * Reads a non-empty string line from standard input.
     */
    public static String readLine(String prompt) {
        return readLine(prompt, false);
    }

    /**
     * Reads a string with option to permit blank input.
     */
    public static String readLine(String prompt, boolean allowEmpty) {
        while (true) {
            System.out.print(prompt);
            if (!SCANNER.hasNextLine()) {
                System.out.println("\n[INFO] End of input reached. Terminating session gracefully.");
                System.exit(0);
            }
            String line = SCANNER.nextLine();
            if (line != null) {
                line = line.trim();
                if (!line.isEmpty() || allowEmpty) {
                    return line;
                }
            }
            System.out.println("Input cannot be empty. Please enter a valid value.");
        }
    }

    /**
     * Reads an integer with exception safety.
     */
    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!SCANNER.hasNextLine()) {
                System.out.println("\n[INFO] End of input reached. Terminating session gracefully.");
                System.exit(0);
            }
            String input = SCANNER.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid numeric input. Please enter a valid integer.");
            }
        }
    }

    /**
     * Reads an integer constrained within a specific inclusive range.
     */
    public static int readInt(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            System.out.println("Choice out of range! Please enter a number between " + min + " and " + max + ".");
        }
    }

    /**
     * Reads a double value with exception safety.
     */
    public static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!SCANNER.hasNextLine()) {
                System.out.println("\n[INFO] End of input reached. Terminating session gracefully.");
                System.exit(0);
            }
            String input = SCANNER.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid number. Please enter a valid decimal number.");
            }
        }
    }

    /**
     * Reads a yes/no confirmation from the user.
     */
    public static boolean readConfirmation(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            if (!SCANNER.hasNextLine()) {
                return false;
            }
            String input = SCANNER.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) return true;
            if (input.equals("n") || input.equals("no")) return false;
            System.out.println("Please enter 'y' for yes or 'n' for no.");
        }
    }

    /**
     * Reads password from terminal using standard Scanner input.
     * Ensures compatibility across all IDEs and terminal environments (especially VS Code terminal),
     * so characters can be typed normally without silent input masking or terminal freezing.
     */
    public static String readPassword(String prompt) {
        return readLine(prompt, false);
    }

    /**
     * Prompts user to press enter to resume navigation.
     */
    public static void pressEnterToContinue() {
        System.out.print("\nPress [ENTER] to continue...");
        if (SCANNER.hasNextLine()) {
            SCANNER.nextLine();
        }
    }
}
