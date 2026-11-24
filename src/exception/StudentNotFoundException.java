package exception;

/**
 * Thrown when a student ID does not match any registered student in the system.
 */
public class StudentNotFoundException extends LibraryException {
    private static final long serialVersionUID = 1L;

    public StudentNotFoundException(String message) {
        super(message);
    }
}
