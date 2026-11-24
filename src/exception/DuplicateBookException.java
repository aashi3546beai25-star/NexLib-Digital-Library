package exception;

/**
 * Thrown when attempting to add a book with an ID or RFID tag that already exists in the catalog.
 */
public class DuplicateBookException extends LibraryException {
    private static final long serialVersionUID = 1L;

    public DuplicateBookException(String message) {
        super(message);
    }
}
