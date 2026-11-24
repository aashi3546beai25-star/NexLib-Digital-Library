package exception;

/**
 * Thrown when user input is invalid, blank, out of bounds, or fails validation checks.
 */
public class InvalidInputException extends LibraryException {
    private static final long serialVersionUID = 1L;

    public InvalidInputException(String message) {
        super(message);
    }
}
