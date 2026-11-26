package util;

import java.util.Scanner;

/**
 * Crash-safe terminal input scanner.
 */
public class InputHelper {
    private static final Scanner sc = new Scanner(System.in);
    private InputHelper() {}

    public static String readLine(String msg) {
        System.out.print(msg);
        return sc.nextLine().trim();
    }

    public static int readInt(String msg, int min, int max) {
        while (true) {
            System.out.print(msg);
            try {
                int val = Integer.parseInt(sc.nextLine().trim());
                if (val >= min && val <= max) return val;
            } catch (NumberFormatException ignored) {}
            System.out.println("Please enter a valid choice between " + min + " and " + max + ".");
        }
    }

    public static boolean readConfirmation(String msg) {
        System.out.print(msg + " y/n: ");
        return sc.nextLine().trim().equalsIgnoreCase("y");
    }

    public static void pressEnterToContinue() {
        System.out.print("\nPress Enter to continue...");
        sc.nextLine();
    }
}
