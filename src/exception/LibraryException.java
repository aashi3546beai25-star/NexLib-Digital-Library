package exception;

/**
 * Root checked exception for NexLib (Lectures 13-15).
 */
public class LibraryException extends Exception {
    public LibraryException(String message) { super(message); }

    public static class StudentNotFoundException extends LibraryException {
        public StudentNotFoundException(String message) { super(message); }
    }

    public static class InvalidInputException extends LibraryException {
        public InvalidInputException(String message) { super(message); }
    }
}
