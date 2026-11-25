package exception;

/**
 * Thrown when a book cannot be issued because it is checked out or reserved.
 */
public class BookUnavailableException extends LibraryException {
    private static final long serialVersionUID = 1L;

    public BookUnavailableException(String message) {
        super(message);
    }
}
