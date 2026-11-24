package service;

import exception.LibraryException;
import model.Book;
import model.FineRecord;
import model.IssueRecord;
import model.Librarian;
import model.Reservation;
import model.Student;

import java.util.List;

/**
 * Interface establishing the contract for digital library operations and circulation services.
 * Demonstrates OOP Abstraction and Interface-driven architecture.
 */
public interface ILibraryService {

    // Book Management
    void addBook(Book book) throws LibraryException;
    void updateBook(String bookId, String newTitle, String newAuthor,
                    String newCategory, String newShelf) throws LibraryException;
    void deleteBook(String bookId) throws LibraryException;
    Book findBookById(String id);
    Book findBookByRfid(String rfid);
    List<Book> getBooks();
    List<Book> searchBooks(String query);
    List<Book> searchBooks(String query, String category);

    // Student Management
    void registerStudent(Student student) throws LibraryException;
    Student findStudent(String id);
    List<Student> getStudents();

    // Circulation & RFID Lending
    IssueRecord issueBook(String studentId, String rfid) throws LibraryException;
    double returnBook(String studentId, String rfid) throws LibraryException;
    List<IssueRecord> getHistory(String studentId);
    List<IssueRecord> getAllIssueRecords();
    List<IssueRecord> getCurrentlyIssuedRecords();

    // Reservation Management
    Reservation reserveBook(String studentId, String bookId) throws LibraryException;
    int getReservationPosition(Reservation reservation);
    List<Reservation> getReservations();
    void cancelReservation(String reservationId, String studentId) throws LibraryException;

    // Fine Management
    List<FineRecord> getFineRecords();
    List<FineRecord> getStudentFineRecords(String studentId);
    double getStudentPendingFineTotal(String studentId);
    boolean payStudentFine(String studentId) throws LibraryException;

    // Authentication Services
    Librarian authenticateLibrarian(String username, String password);
    Student authenticateStudent(String studentId, String password);
}
