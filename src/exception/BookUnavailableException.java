package exception;

/**
 * Thrown when a book is already checked out (Lectures 13-15).
 */
public class BookUnavailableException extends LibraryException {
    public BookUnavailableException(String message) { super(message); }
}
