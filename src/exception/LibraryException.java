package exception;

/**
 * Custom exception hierarchy for NexLib library operations.
 * Defines domain-specific exceptions as static nested classes within one clean file.
 */
public class LibraryException extends Exception {
    private static final long serialVersionUID = 1L;

    public LibraryException(String message) {
        super(message);
    }

    public static class BookNotFoundException extends LibraryException {
        public BookNotFoundException(String message) {
            super(message);
        }
    }

    public static class BookUnavailableException extends LibraryException {
        public BookUnavailableException(String message) {
            super(message);
        }
    }

    public static class InvalidRFIDException extends LibraryException {
        public InvalidRFIDException(String message) {
            super(message);
        }
    }

    public static class StudentNotFoundException extends LibraryException {
        public StudentNotFoundException(String message) {
            super(message);
        }
    }

    public static class DuplicateBookException extends LibraryException {
        public DuplicateBookException(String message) {
            super(message);
        }
    }

    public static class InvalidInputException extends LibraryException {
        public InvalidInputException(String message) {
            super(message);
        }
    }
}
