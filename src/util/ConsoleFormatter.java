package util;

/**
 * Utility class providing ANSI-free, professional ASCII table formatting,
 * headers, dividers, and status indicators for the terminal interface.
 * Ensures cross-platform compatibility across all OS terminals without external UI libraries.
 */
public class ConsoleFormatter {

    private ConsoleFormatter() { }

    public static void printHeader(String title) {
        int width = 76;
        String line = "=".repeat(width);
        System.out.println("\n" + line);
        int pad = Math.max(0, (width - title.length()) / 2);
        System.out.println(" ".repeat(pad) + title);
        System.out.println(line);
    }

    public static void printSubHeader(String subtitle) {
        int width = 76;
        String line = "-".repeat(width);
        System.out.println("\n" + line);
        int pad = Math.max(0, (width - subtitle.length()) / 2);
        System.out.println(" ".repeat(pad) + subtitle);
        System.out.println(line);
    }

    public static void printDivider() {
        System.out.println("-".repeat(76));
    }

    public static void printSuccess(String message) {
        System.out.println("\n[SUCCESS] " + message);
    }

    public static void printError(String message) {
        System.out.println("\n[ERROR] " + message);
    }

    public static void printWarning(String message) {
        System.out.println("\n[WARNING] " + message);
    }

    public static void printInfo(String message) {
        System.out.println("\n[INFO] " + message);
    }

    public static void printRfidPrompt(String message) {
        System.out.println("\n>>> [RFID HARDWARE EMULATOR] " + message);
    }
}
