package exception;

/**
 * Thrown when a requested book cannot be located in the library catalog by ID or RFID.
 */
public class BookNotFoundException extends LibraryException {
    private static final long serialVersionUID = 1L;

    public BookNotFoundException(String message) {
        super(message);
    }
}
