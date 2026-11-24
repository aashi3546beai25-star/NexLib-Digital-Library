package exception;

/**
 * Thrown when an operation requires an available book, but the book is currently
 * checked out, reserved, or otherwise unavailable for lending.
 */
public class BookUnavailableException extends LibraryException {
    private static final long serialVersionUID = 1L;

    public BookUnavailableException(String message) {
        super(message);
    }
}
