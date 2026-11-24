package exception;

/**
 * Base checked exception for all NexLib library management operations.
 * Demonstrates Java Exception hierarchy and custom exception handling.
 */
public class LibraryException extends Exception {
    private static final long serialVersionUID = 1L;

    public LibraryException(String message) {
        super(message);
    }

    public LibraryException(String message, Throwable cause) {
        super(message, cause);
    }
}
