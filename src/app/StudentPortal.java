package app;

import exception.LibraryException;
import model.Book;
import model.IssueRecord;
import model.Reservation;
import model.Student;
import service.LibraryService;
import storage.DataStore;
import util.ConsoleFormatter;
import util.InputHelper;
import util.RFIDScanner;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for Student self-service circulation and dashboard.
 */
public class StudentPortal {
    private final LibraryService library;
    private final DataStore store;

    public StudentPortal(LibraryService library, DataStore store) {
        this.library = library;
        this.store = store;
    }

    public void login() {
        ConsoleFormatter.printSubHeader("STUDENT SELF-SERVICE PORTAL LOGIN");
        System.out.println("Default Demo Accounts: ID = STU101 (or STU102, STU103) | Password = pass123");

        for (int attempts = 1; attempts <= 3; attempts++) {
            String id = InputHelper.readLine("Enter Student ID: ");
            String pass = InputHelper.readPassword("Enter Password: ");

            Student student = library.authenticateStudent(id, pass);
            if (student != null) {
                System.out.println("\n[SUCCESS] Welcome, " + student.getName() + " (" + student.getDepartment() + ")!");
                menu(student);
                return;
            }
            System.out.println("\n[ERROR] Invalid student credentials! Remaining attempts: " + (3 - attempts));
            if (library.findStudent(id) == null) {
                System.out.println("[TIP] Student ID '" + id + "' is not registered. Register via Librarian (Option 3).");
            } else {
                System.out.println("[TIP] Default password is: pass123");
            }
        }
        System.out.println("\n[WARNING] Authentication failed. Returning to main menu.");
        InputHelper.pressEnterToContinue();
    }

    public void menu(Student student) {
        boolean active = true;
        while (active) {
            ConsoleFormatter.printHeader("STUDENT DASHBOARD | " + student.getName() + " (" + student.getId() + ")");
            System.out.println(" 1. View Available Books For Borrowing");
            System.out.println(" 2. Search Book Catalog");
            System.out.println(" 3. Self-Service Borrow Book (RFID Scanner)");
            System.out.println(" 4. Self-Service Return Book (RFID Scanner)");
            System.out.println(" 5. Reserve an Unavailable Book (Join Queue)");
            System.out.println(" 6. View My Currently Borrowed Books & Due Dates");
            System.out.println(" 7. View My Complete Borrowing History");
            System.out.println(" 8. Check My Pending Fines & Pay Fine");
            System.out.println(" 9. View My Profile");
            System.out.println("10. Logout");
            ConsoleFormatter.printDivider();

            int choice = InputHelper.readInt("Enter choice (1-10): ", 1, 10);
            try {
                switch (choice) {
                    case 1 -> displayAvailableBooks();
                    case 2 -> searchBooks();
                    case 3 -> issueBookFlow(student.getId());
                    case 4 -> returnBookFlow(student.getId());
                    case 5 -> reserveBookFlow(student.getId());
                    case 6 -> displayStudentLoans(student);
                    case 7 -> { ConsoleFormatter.printIssueTable(library.getHistory(student.getId())); InputHelper.pressEnterToContinue(); }
                    case 8 -> displayStudentFines(student);
                    case 9 -> { student.displayProfile(); InputHelper.pressEnterToContinue(); }
                    case 10 -> { active = false; System.out.println("\n[INFO] Logged out of Student Portal."); }
                }
                store.save(library);
            } catch (LibraryException ex) {
                System.out.println("\n[ERROR] Operation Failed: " + ex.getMessage());
                InputHelper.pressEnterToContinue();
            }
        }
    }

    private void displayAvailableBooks() {
        List<Book> available = new ArrayList<>();
        for (Book b : library.getBooks()) {
            if (b.isAvailable()) available.add(b);
        }
        ConsoleFormatter.printBookTable(available);
        System.out.println("Available for borrowing: " + available.size() + " book(s).");
        InputHelper.pressEnterToContinue();
    }

    private void searchBooks() {
        ConsoleFormatter.printSubHeader("SEARCH BOOK CATALOG");
        System.out.println("1. General Search (Title, Author, Category, ID) | 2. Filter by Category");
        int mode = InputHelper.readInt("Select mode (1-2): ", 1, 2);

        List<Book> results;
        if (mode == 1) {
            String q = InputHelper.readLine("Enter search keyword: ");
            results = library.searchBooks(q);
        } else {
            String cat = InputHelper.readLine("Enter Category: ");
            String q = InputHelper.readLine("Enter Keyword (optional): ", true);
            results = library.searchBooks(q, cat);
        }
        ConsoleFormatter.printBookTable(results);
        InputHelper.pressEnterToContinue();
    }

    private void issueBookFlow(String studentId) throws LibraryException {
        ConsoleFormatter.printSubHeader("SELF-SERVICE BORROW (RFID)");
        String rfid = RFIDScanner.scan("Scan book RFID tag: ");
        IssueRecord record = library.issueBook(studentId, rfid);

        System.out.println("\n[SUCCESS] BOOK ISSUED SUCCESSFULLY!");
        System.out.println("Txn ID   : " + record.getRecordId());
        System.out.println("Book     : " + record.getBookTitle() + " (ID: " + record.getBookId() + ")");
        System.out.println("Due Date : " + record.getDueDate() + " (Borrow Period: 7 days)");
        InputHelper.pressEnterToContinue();
    }

    private void returnBookFlow(String studentId) throws LibraryException {
        ConsoleFormatter.printSubHeader("SELF-SERVICE RETURN (RFID)");
        String rfid = RFIDScanner.scan("Scan returned book RFID tag: ");
        double fine = library.returnBook(studentId, rfid);

        System.out.println("\n[SUCCESS] Book returned successfully!");
        if (fine > 0) {
            System.out.println("\n[WARNING] Overdue fine assessed: Rs. " + fine);
        } else {
            System.out.println("\n[INFO] Returned on time. No fine.");
        }
        InputHelper.pressEnterToContinue();
    }

    private void reserveBookFlow(String studentId) throws LibraryException {
        ConsoleFormatter.printSubHeader("RESERVE BOOK (FIFO WAITING QUEUE)");
        String bookId = InputHelper.readLine("Enter Book ID to reserve (e.g. B101): ");
        Reservation res = library.reserveBook(studentId, bookId);
        int pos = library.getReservationPosition(res);

        System.out.println("\n[SUCCESS] Reservation placed successfully!");
        System.out.println("Reservation ID : " + res.getReservationId());
        System.out.println("Queue Status   : " + (pos == 0 ? "AVAILABLE for immediate pickup!" : ("Position #" + pos + " in line.")));
        InputHelper.pressEnterToContinue();
    }

    private void displayStudentLoans(Student stu) {
        ConsoleFormatter.printSubHeader("CURRENTLY BORROWED BOOKS");
        List<IssueRecord> active = new ArrayList<>();
        for (IssueRecord r : library.getHistory(stu.getId())) {
            if (!r.isReturned()) active.add(r);
        }
        ConsoleFormatter.printIssueTable(active);
        System.out.println("Total Active Loans: " + active.size() + " / " + stu.getMaxBooksAllowed());
        InputHelper.pressEnterToContinue();
    }

    private void displayStudentFines(Student stu) throws LibraryException {
        ConsoleFormatter.printSubHeader("MY FINES & SETTLEMENTS");
        double pending = library.getStudentPendingFineTotal(stu.getId());
        if (pending <= 0) {
            System.out.println("\n[INFO] No pending fines on your account.");
        } else {
            System.out.printf("Total Outstanding Fine: Rs. %.2f%n", pending);
            if (InputHelper.readConfirmation("Pay your pending fine now?")) {
                library.payStudentFine(stu.getId());
                System.out.println("\n[SUCCESS] All pending dues cleared!");
            }
        }
        InputHelper.pressEnterToContinue();
    }
}
