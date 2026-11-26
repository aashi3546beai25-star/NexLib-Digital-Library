package exception;

/**
 * Thrown when adding a duplicate book ID (Lectures 13-15).
 */
public class DuplicateBookException extends LibraryException {
    public DuplicateBookException(String message) { super(message); }
}
