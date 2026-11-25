package util;

/**
 * Basic terminal simulation of an RFID tag scanner.
 * Asks the user to enter the RFID tag of the book.
 */
public class RFIDScanner {

    private RFIDScanner() { }

    /**
     * Default simulated scan method.
     */
    public static String scan() {
        return scan("Enter book RFID tag: ");
    }

    /**
     * Overloaded scan method with custom prompt.
     */
    public static String scan(String customPrompt) {
        System.out.println("\n[RFID SCANNER SIMULATION]");
        String tag = InputHelper.readLine(customPrompt).toUpperCase();
        System.out.println("Scanned Tag UID: [" + tag + "]");
        return tag;
    }

    /**
     * Generates a simple RFID tag string for a book ID.
     */
    public static String generateTag(String bookId) {
        return "RFID-" + bookId.toUpperCase();
    }
}
