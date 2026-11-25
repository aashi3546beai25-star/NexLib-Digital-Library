package app;

import exception.LibraryException;
import model.*;
import service.LibraryService;
import storage.DataStore;
import util.InputHelper;
import util.RFIDScanner;

import java.util.ArrayList;
import java.util.List;

/**
 * Menu-driven terminal application controller for NexLib.
 * Coordinates user menus for Librarian and Student portals.
 */
public class LibraryApp {
    private final LibraryService library;
    private final DataStore store;

    public LibraryApp(LibraryService library, DataStore store) {
        this.library = library;
        this.store = store;
    }

    // =========================================================================
    // MAIN ENTRY LOOP
    // =========================================================================

    public void start() {
        boolean running = true;
        while (running) {
            printHeader("NEXLIB - DIGITAL COLLEGE LIBRARY & RFID BOOK LENDING SYSTEM");
            System.out.println("  1. Librarian Portal (Administrative Access)");
            System.out.println("  2. Student Portal (Self-Service Circulation)");
            System.out.println("  3. Browse Catalog & Search Books (Open Guest Access)");
            System.out.println("  4. Architecture & Viva Demonstration Guide");
            System.out.println("  5. Save & Exit");
            printDivider();

            int choice = InputHelper.readInt("Select portal option (1-5): ", 1, 5);
            switch (choice) {
                case 1 -> librarianLogin();
                case 2 -> studentLogin();
                case 3 -> guestBrowseCatalog();
                case 4 -> displayVivaGuide();
                case 5 -> {
                    store.save(library);
                    System.out.println("\n[SUCCESS] All library records safely saved to disk.");
                    System.out.println("Thank you for using NexLib. Goodbye!");
                    running = false;
                }
            }
        }
    }

    // =========================================================================
    // LIBRARIAN PORTAL
    // =========================================================================

    private void librarianLogin() {
        printSubHeader("LIBRARIAN SECURE LOGIN");
        System.out.println("(Default Credentials: Username = admin | Password = admin123)");

        for (int attempts = 1; attempts <= 3; attempts++) {
            String user = InputHelper.readLine("Enter Librarian Username: ");
            String pass = InputHelper.readPassword("Enter Password: ");

            Librarian librarian = library.authenticateLibrarian(user, pass);
            if (librarian != null) {
                System.out.println("\n[SUCCESS] Welcome, " + librarian.getName() + " (" + librarian.getDesignation() + ")!");
                librarianMenu(librarian);
                return;
            }
            System.out.println("\n[ERROR] Invalid credentials! Remaining attempts: " + (3 - attempts));
        }
        System.out.println("\n[WARNING] Maximum login attempts exceeded. Returning to main menu.");
        InputHelper.pressEnterToContinue();
    }

    private void librarianMenu(Librarian librarian) {
        boolean active = true;
        while (active) {
            printHeader("LIBRARIAN CONSOLE | Staff: " + librarian.getName());
            System.out.println(" 1. Add Book (With RFID)       9. Accept Returned Book");
            System.out.println(" 2. Update Book Details       10. View Currently Issued Books");
            System.out.println(" 3. Delete Book               11. View Complete Borrowing History");
            System.out.println(" 4. View All Books In Catalog 12. Manage Book Reservations");
            System.out.println(" 5. Search Books              13. View Overdue Fines & Collect");
            System.out.println(" 6. Add Student (Register)    14. Generate Audit Reports");
            System.out.println(" 7. View All Students         15. View My Staff Profile");
            System.out.println(" 8. Issue Book (RFID Scan)    16. Logout");
            printDivider();

            int choice = InputHelper.readInt("Enter choice (1-16): ", 1, 16);
            try {
                switch (choice) {
                    case 1 -> addBook();
                    case 2 -> updateBook();
                    case 3 -> deleteBook();
                    case 4 -> { printBookTable(library.getBooks()); InputHelper.pressEnterToContinue(); }
                    case 5 -> searchBooks();
                    case 6 -> registerStudent();
                    case 7 -> displayStudents();
                    case 8 -> issueBookFlow(null);
                    case 9 -> returnBookFlow(null);
                    case 10 -> { printIssueTable(library.getCurrentlyIssuedRecords()); InputHelper.pressEnterToContinue(); }
                    case 11 -> displayHistoryFlow();
                    case 12 -> manageReservations();
                    case 13 -> manageFines();
                    case 14 -> generateReports();
                    case 15 -> { librarian.displayProfile(); InputHelper.pressEnterToContinue(); }
                    case 16 -> { active = false; System.out.println("\n[INFO] Librarian logged out."); }
                }
                store.save(library); // Auto-save changes
            } catch (LibraryException ex) {
                System.out.println("\n[ERROR] Operation Failed: " + ex.getMessage());
                InputHelper.pressEnterToContinue();
            }
        }
    }

    // =========================================================================
    // STUDENT PORTAL
    // =========================================================================

    private void studentLogin() {
        printSubHeader("STUDENT SELF-SERVICE PORTAL LOGIN");
        System.out.println("Default Demo Accounts: ID = STU101 (or STU102, STU103) | Password = pass123");

        for (int attempts = 1; attempts <= 3; attempts++) {
            String id = InputHelper.readLine("Enter Student ID: ");
            String pass = InputHelper.readPassword("Enter Password: ");

            Student student = library.authenticateStudent(id, pass);
            if (student != null) {
                System.out.println("\n[SUCCESS] Welcome, " + student.getName() + " (" + student.getDepartment() + ")!");
                studentMenu(student);
                return;
            }
            System.out.println("\n[ERROR] Invalid student credentials! Remaining attempts: " + (3 - attempts));
            if (library.findStudent(id) == null) {
                System.out.println("[TIP] Student ID '" + id + "' is not registered. Register via Librarian (Option 6).");
            } else {
                System.out.println("[TIP] Default password is: pass123");
            }
        }
        System.out.println("\n[WARNING] Authentication failed. Returning to main menu.");
        InputHelper.pressEnterToContinue();
    }

    private void studentMenu(Student student) {
        boolean active = true;
        while (active) {
            printHeader("STUDENT DASHBOARD | " + student.getName() + " (" + student.getId() + ")");
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
            printDivider();

            int choice = InputHelper.readInt("Enter choice (1-10): ", 1, 10);
            try {
                switch (choice) {
                    case 1 -> displayAvailableBooks();
                    case 2 -> searchBooks();
                    case 3 -> issueBookFlow(student.getId());
                    case 4 -> returnBookFlow(student.getId());
                    case 5 -> reserveBookFlow(student.getId());
                    case 6 -> displayStudentLoans(student);
                    case 7 -> { printIssueTable(library.getHistory(student.getId())); InputHelper.pressEnterToContinue(); }
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

    // =========================================================================
    // REUSABLE WORKFLOWS
    // =========================================================================

    private void addBook() throws LibraryException {
        printSubHeader("ADD NEW BOOK TO CATALOG");
        String id = InputHelper.readLine("Enter Unique Book ID (e.g. B107): ");
        String title = InputHelper.readLine("Enter Title: ");
        String author = InputHelper.readLine("Enter Author: ");
        String category = InputHelper.readLine("Enter Category: ");
        String shelf = InputHelper.readLine("Enter Shelf Location (e.g. Rack-CS-1): ");

        String suggestedRfid = RFIDScanner.generateTag(id);
        System.out.println("Default Tag UID: " + suggestedRfid);
        String rfid = InputHelper.readLine("Enter/Confirm RFID Tag UID: ");
        if (rfid.isEmpty()) rfid = suggestedRfid;

        library.addBook(new Book(id, title, author, category, rfid, shelf));
        System.out.println("\n[SUCCESS] Book successfully added and mapped to RFID: [" + rfid + "].");
        InputHelper.pressEnterToContinue();
    }

    private void updateBook() throws LibraryException {
        printSubHeader("UPDATE BOOK DETAILS");
        String id = InputHelper.readLine("Enter Book ID to update: ");
        Book b = library.findBookById(id);
        if (b == null) {
            System.out.println("\n[ERROR] No book found with ID: " + id);
            InputHelper.pressEnterToContinue();
            return;
        }
        System.out.println("Current: " + b.getTitle() + " | " + b.getAuthor() + " | " + b.getCategory());
        String title = InputHelper.readLine("New Title (or enter to keep): ", true);
        String author = InputHelper.readLine("New Author (or enter to keep): ", true);
        String category = InputHelper.readLine("New Category (or enter to keep): ", true);
        String shelf = InputHelper.readLine("New Shelf (or enter to keep): ", true);

        library.updateBook(id, title, author, category, shelf);
        System.out.println("\n[SUCCESS] Book updated successfully.");
        InputHelper.pressEnterToContinue();
    }

    private void deleteBook() throws LibraryException {
        printSubHeader("DELETE BOOK");
        String id = InputHelper.readLine("Enter Book ID to delete: ");
        if (InputHelper.readConfirmation("Confirm permanent deletion of book ID '" + id + "'?")) {
            library.deleteBook(id);
            System.out.println("\n[SUCCESS] Book removed from catalog.");
        }
        InputHelper.pressEnterToContinue();
    }

    private void searchBooks() {
        printSubHeader("SEARCH BOOK CATALOG");
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
        printBookTable(results);
        InputHelper.pressEnterToContinue();
    }

    private void registerStudent() throws LibraryException {
        printSubHeader("ADD NEW STUDENT");
        String id = InputHelper.readLine("Enter Student ID (e.g. STU104): ");
        String name = InputHelper.readLine("Enter Student Name: ");
        String email = InputHelper.readLine("Enter Email: ");
        String pass = InputHelper.readPassword("Set Password: ");
        String dept = InputHelper.readLine("Enter Department: ");
        String phone = InputHelper.readLine("Enter Phone: ");
        int limit = InputHelper.readInt("Enter Borrow Limit (Default 3): ");

        library.registerStudent(new Student(id, name, email, pass, dept, phone, limit));
        System.out.println("\n[SUCCESS] Student added successfully! Can now log in using ID: " + id);
        InputHelper.pressEnterToContinue();
    }

    private void displayStudents() {
        printSubHeader("REGISTERED STUDENTS DIRECTORY");
        List<Student> list = library.getStudents();
        if (list.isEmpty()) {
            System.out.println("No students registered.");
        } else {
            System.out.printf("%-10s | %-20s | %-22s | %-15s%n", "STUDENT ID", "STUDENT NAME", "EMAIL", "DEPARTMENT");
            printDivider();
            for (Student s : list) System.out.println(s);
            System.out.println("Total Registered Students: " + list.size());
        }
        InputHelper.pressEnterToContinue();
    }

    private void issueBookFlow(String studentId) throws LibraryException {
        printSubHeader("ISSUE BOOK (RFID ASSISTED)");
        if (studentId == null) {
            studentId = InputHelper.readLine("Enter Student ID: ");
        }
        String rfid = RFIDScanner.scan("Scan book RFID tag: ");
        IssueRecord record = library.issueBook(studentId, rfid);

        System.out.println("\n[SUCCESS] BOOK ISSUED SUCCESSFULLY!");
        System.out.println("Txn ID   : " + record.getRecordId());
        System.out.println("Book     : " + record.getBookTitle() + " (ID: " + record.getBookId() + ")");
        System.out.println("Due Date : " + record.getDueDate() + " (Borrow Period: 7 days)");
        InputHelper.pressEnterToContinue();
    }

    private void returnBookFlow(String studentId) throws LibraryException {
        printSubHeader("RETURN BOOK (RFID ASSISTED)");
        if (studentId == null) {
            studentId = InputHelper.readLine("Enter Student ID: ");
        }
        String rfid = RFIDScanner.scan("Scan returned book RFID tag: ");
        double fine = library.returnBook(studentId, rfid);

        System.out.println("\n[SUCCESS] Book returned successfully to library stock!");
        if (fine > 0) {
            System.out.println("\n[WARNING] Overdue fine assessed: Rs. " + fine);
        } else {
            System.out.println("\n[INFO] Returned on time. No overdue fine.");
        }
        InputHelper.pressEnterToContinue();
    }

    private void reserveBookFlow(String studentId) throws LibraryException {
        printSubHeader("RESERVE BOOK (FIFO WAITING QUEUE)");
        String bookId = InputHelper.readLine("Enter Book ID to reserve (e.g. B101): ");
        Reservation res = library.reserveBook(studentId, bookId);
        int pos = library.getReservationPosition(res);

        System.out.println("\n[SUCCESS] Reservation placed successfully!");
        System.out.println("Reservation ID : " + res.getReservationId());
        System.out.println("Queue Status   : " + (pos == 0 ? "AVAILABLE for immediate pickup!" : ("Position #" + pos + " in line.")));
        InputHelper.pressEnterToContinue();
    }

    private void manageReservations() throws LibraryException {
        printSubHeader("RESERVATION QUEUE");
        List<Reservation> list = library.getReservations();
        if (list.isEmpty()) {
            System.out.println("No reservation requests.");
        } else {
            System.out.printf("%-10s | %-10s | %-16s | %-8s | %-12s | %-10s%n", "RES ID", "STUDENT ID", "STUDENT NAME", "BOOK ID", "DATE", "STATUS");
            printDivider();
            for (Reservation r : list) System.out.println(r);

            if (InputHelper.readConfirmation("Would you like to cancel a reservation?")) {
                String id = InputHelper.readLine("Enter Reservation ID to cancel: ");
                library.cancelReservation(id, null);
                System.out.println("\n[SUCCESS] Reservation cancelled.");
            }
        }
        InputHelper.pressEnterToContinue();
    }

    private void manageFines() throws LibraryException {
        printSubHeader("OVERDUE FINES MANAGEMENT");
        List<IssueRecord> issued = library.getAllIssueRecords();
        List<IssueRecord> withFines = new ArrayList<>();
        for (IssueRecord r : issued) {
            if (r.getFine() > 0) {
                withFines.add(r);
            }
        }
        if (withFines.isEmpty()) {
            System.out.println("No overdue fine records found. All accounts clear.");
        } else {
            printIssueTable(withFines);
            if (InputHelper.readConfirmation("Collect/Settle a student's pending fine?")) {
                String stuId = InputHelper.readLine("Enter Student ID: ");
                library.payStudentFine(stuId);
                System.out.println("\n[SUCCESS] Fines settled successfully.");
            }
        }
        InputHelper.pressEnterToContinue();
    }

    private void generateReports() {
        printSubHeader("LIBRARY AUDIT REPORTS");
        System.out.println("1. Executive Summary | 2. Overdue Audit | 3. Category Breakdown");
        int r = InputHelper.readInt("Select report (1-3): ", 1, 3);
        switch (r) {
            case 1 -> library.printExecutiveSummary();
            case 2 -> library.printOverdueReport();
            case 3 -> library.printCategoryDistribution();
        }
        InputHelper.pressEnterToContinue();
    }

    private void displayHistoryFlow() {
        System.out.println("1. View All Circulation History | 2. Filter by Student ID");
        int opt = InputHelper.readInt("Select (1-2): ", 1, 2);
        List<IssueRecord> records = (opt == 1) ? library.getAllIssueRecords() : library.getHistory(InputHelper.readLine("Enter Student ID: "));
        printIssueTable(records);
        InputHelper.pressEnterToContinue();
    }

    private void displayAvailableBooks() {
        List<Book> available = new ArrayList<>();
        for (Book b : library.getBooks()) {
            if (b.isAvailable()) available.add(b);
        }
        printBookTable(available);
        System.out.println("Available for borrowing: " + available.size() + " book(s).");
        InputHelper.pressEnterToContinue();
    }

    private void displayStudentLoans(Student stu) {
        printSubHeader("CURRENTLY BORROWED BOOKS");
        List<IssueRecord> active = new ArrayList<>();
        for (IssueRecord r : library.getHistory(stu.getId())) {
            if (!r.isReturned()) active.add(r);
        }
        printIssueTable(active);
        System.out.println("Total Active Loans: " + active.size() + " / " + stu.getMaxBooksAllowed());
        InputHelper.pressEnterToContinue();
    }

    private void displayStudentFines(Student stu) throws LibraryException {
        printSubHeader("MY FINES & SETTLEMENTS");
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

    private void guestBrowseCatalog() {
        boolean back = false;
        while (!back) {
            printHeader("OPEN CATALOG BROWSER (GUEST ACCESS)");
            System.out.println(" 1. View All Books In Catalog");
            System.out.println(" 2. Search Books");
            System.out.println(" 3. View Available Books");
            System.out.println(" 4. Back to Main Menu");
            printDivider();
            int c = InputHelper.readInt("Choice (1-4): ", 1, 4);
            switch (c) {
                case 1 -> { printBookTable(library.getBooks()); InputHelper.pressEnterToContinue(); }
                case 2 -> searchBooks();
                case 3 -> displayAvailableBooks();
                case 4 -> back = true;
            }
        }
    }

    private void displayVivaGuide() {
        printHeader("NEXLIB TECHNICAL ARCHITECTURE & VIVA GUIDE");
        System.out.println("1. OOP Concepts: Classes/Objects, Encapsulation, Inheritance (User -> Student/Librarian),");
        System.out.println("   Abstraction (User & LibraryService), Polymorphism (displayProfile, searchBooks overloads).");
        System.out.println("2. Collections: ArrayList (lists), HashMap (O(1) RFID lookup), Queue/LinkedList (FIFO reservations).");
        System.out.println("3. Custom Exceptions: BookNotFoundException, BookUnavailableException, DuplicateBookException, etc.");
        System.out.println("4. Persistence: Java Object Serialization (DataStore) with atomic .bak backup.");
        System.out.println("5. Hardware Simulation: RFIDScanner simulates 13.56 MHz RFID readers via console.");
        printDivider();
        InputHelper.pressEnterToContinue();
    }

    // =========================================================================
    // REUSABLE PRINT HELPERS
    // =========================================================================

    public static void printHeader(String title) {
        System.out.println("\n============================================================================");
        System.out.println("  " + title);
        System.out.println("============================================================================");
    }

    public static void printSubHeader(String subtitle) {
        System.out.println("\n----------------------------------------------------------------------------");
        System.out.println("  " + subtitle);
        System.out.println("----------------------------------------------------------------------------");
    }

    public static void printDivider() {
        System.out.println("----------------------------------------------------------------------------");
    }

    private void printBookTable(List<Book> books) {
        if (books == null || books.isEmpty()) {
            System.out.println("No matching books found.");
            return;
        }
        System.out.printf("%-8s | %-26s | %-18s | %-15s | %-12s | %-9s%n",
                "BOOK ID", "TITLE", "AUTHOR", "CATEGORY", "RFID TAG", "STATUS");
        printDivider();
        for (Book b : books) System.out.println(b);
        System.out.println("Book Count: " + books.size());
    }

    private void printIssueTable(List<IssueRecord> records) {
        if (records == null || records.isEmpty()) {
            System.out.println("No circulation records found.");
            return;
        }
        System.out.printf("%-10s | %-10s | %-16s | %-8s | %-11s | %-11s | %-11s | %-8s | %-10s%n",
                "TXN ID", "STUDENT ID", "STUDENT NAME", "BOOK ID", "ISSUE DATE", "DUE DATE", "RETURN DATE", "STATUS", "FINE");
        printDivider();
        for (IssueRecord r : records) System.out.println(r);
    }
}
