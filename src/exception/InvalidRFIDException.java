package exception;

/**
 * Thrown when an RFID tag scanned or entered does not adhere to expected formatting
 * or cannot be linked to any physical asset in the library.
 */
public class InvalidRFIDException extends LibraryException {
    private static final long serialVersionUID = 1L;

    public InvalidRFIDException(String message) {
        super(message);
    }
}
