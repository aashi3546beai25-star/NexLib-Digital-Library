package exception;

/**
 * Thrown when a Book ID cannot be found in catalog (Lectures 13-15).
 */
public class BookNotFoundException extends LibraryException {
    public BookNotFoundException(String message) { super(message); }
}
