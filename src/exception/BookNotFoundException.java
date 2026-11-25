package exception;

/**
 * Thrown when a requested book catalog ID or RFID tag cannot be located.
 */
public class BookNotFoundException extends LibraryException {
    private static final long serialVersionUID = 1L;

    public BookNotFoundException(String message) {
        super(message);
    }
}
