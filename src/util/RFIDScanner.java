package util;

/**
 * Terminal-based emulator for physical High-Frequency (HF) / UHF RFID Readers.
 * Simulates book transponder scanning via interactive console input.
 */
public class RFIDScanner {

    private RFIDScanner() { }

    /**
     * Primary simulated scan method. Prompts user to scan or enter tag ID.
     */
    public static String scan() {
        return scan("Place book on RFID Reader Pad or enter tag ID: ");
    }

    /**
     * Overloaded scan method with customized prompt context.
     */
    public static String scan(String customPrompt) {
        System.out.println("\n+-------------------------------------------------------------+");
        System.out.println("|  [RFID HARDWARE EMULATOR] 13.56 MHz RFID Transponder Reader |");
        System.out.println("|  Status: Antenna ACTIVE | Awaiting Tag Placement...         |");
        System.out.println("+-------------------------------------------------------------+");
        String tag = InputHelper.readLine(">>> " + customPrompt).toUpperCase();
        System.out.println(">>> [RFID BEEP!] Captured Tag UID: [" + tag + "]");
        return tag;
    }

    /**
     * Utility method to generate standard RFID tag string for auto-tagging.
     */
    public static String generateTag(String bookId) {
        String cleanId = bookId.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        return "RFID-" + cleanId;
    }
}
