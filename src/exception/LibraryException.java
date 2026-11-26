package exception;

/**
 * Root checked exception for NexLib with basic nested domain exceptions.
 */
public class LibraryException extends Exception {
    public LibraryException(String message) { super(message); }

    public static class StudentNotFoundException extends LibraryException {
        public StudentNotFoundException(String message) { super(message); }
    }

    public static class InvalidInputException extends LibraryException {
        public InvalidInputException(String message) { super(message); }
    }

    public static class DuplicateBookException extends LibraryException {
        public DuplicateBookException(String message) { super(message); }
    }

    public static class BookNotFoundException extends LibraryException {
        public BookNotFoundException(String message) { super(message); }
    }

    public static class BookUnavailableException extends LibraryException {
        public BookUnavailableException(String message) { super(message); }
    }
}
