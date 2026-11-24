package app;

import exception.LibraryException;
import model.*;
import service.FineService;
import service.ILibraryService;
import service.ReportService;
import service.ReservationService;
import storage.DataStore;
import util.ConsoleFormatter;
import util.InputHelper;
import util.RFIDScanner;

import java.util.List;

/**
 * Menu-driven terminal application controller for NexLib.
 * Coordinates user interactions, presentation logic, input validation,
 * and service invocations across Librarian and Student roles.
 */
public class LibraryApp {
    private final ILibraryService library;
    private final DataStore store;
    private final service.LibraryService concreteService;

    public LibraryApp(service.LibraryService library, DataStore store) {
        this.library = library;
        this.concreteService = library;
        this.store = store;
    }

    /**
     * Main application loop.
     */
    public void start() {
        boolean running = true;

        while (running) {
            ConsoleFormatter.printHeader("NEXLIB - DIGITAL COLLEGE LIBRARY & RFID BOOK LENDING SYSTEM");
            System.out.println("  1. Librarian Portal (Administrative Access)");
            System.out.println("  2. Student Portal (Self-Service Circulation)");
            System.out.println("  3. Browse Catalog & Search Books (Open Guest Access)");
            System.out.println("  4. Architecture & Viva Demonstration Guide");
            System.out.println("  5. Save & Exit");
            ConsoleFormatter.printDivider();

            int choice = InputHelper.readInt("Select portal option (1-5): ", 1, 5);

            switch (choice) {
                case 1 -> librarianLoginFlow();
                case 2 -> studentLoginFlow();
                case 3 -> guestBrowseFlow();
                case 4 -> displayVivaGuide();
                case 5 -> {
                    store.save(concreteService);
                    ConsoleFormatter.printSuccess("All library records, loans, and reservations have been safely persisted.");
                    System.out.println("\nThank you for using NexLib Digital Library System. Goodbye!");
                    running = false;
                }
            }
        }
    }

    // =========================================================================
    // LIBRARIAN PORTAL FLOWS
    // =========================================================================

    private void librarianLoginFlow() {
        ConsoleFormatter.printSubHeader("LIBRARIAN SECURE LOGIN");
        System.out.println("(Default Credentials: Username = admin | Password = admin123)");

        int attempts = 0;
        Librarian librarian = null;

        while (attempts < 3) {
            String username = InputHelper.readLine("Enter Librarian Username/ID: ");
            String password = InputHelper.readPassword("Enter Password: ");

            librarian = library.authenticateLibrarian(username, password);
            if (librarian != null) {
                break;
            }
            attempts++;
            ConsoleFormatter.printError("Invalid credentials! Remaining attempts: " + (3 - attempts));
        }

        if (librarian == null) {
            ConsoleFormatter.printWarning("Maximum login attempts exceeded. Returning to main menu.");
            InputHelper.pressEnterToContinue();
            return;
        }

        ConsoleFormatter.printSuccess("Welcome, " + librarian.getName() + " (" + librarian.getDesignation() + ")!");
        librarianMenu(librarian);
    }

    private void librarianMenu(Librarian librarian) {
        boolean active = true;

        while (active) {
            ConsoleFormatter.printHeader("LIBRARIAN MANAGEMENT CONSOLE | User: " + librarian.getName());
            System.out.println("--- Book Catalog Management ---");
            System.out.println("  1. Add New Book (With RFID Tag)");
            System.out.println("  2. Update Book Details");
            System.out.println("  3. Delete Book From Catalog");
            System.out.println("  4. View All Books In Catalog");
            System.out.println("  5. Search Books (Title, Author, Category, ID)");
            System.out.println("\n--- Student Management ---");
            System.out.println("  6. Register New Student");
            System.out.println("  7. View All Registered Students");
            System.out.println("\n--- Circulation & RFID Lending ---");
            System.out.println("  8. Issue Book (RFID Simulated Scan)");
            System.out.println("  9. Accept Returned Book (RFID Simulated Scan & Fine Calc)");
            System.out.println(" 10. View Currently Issued Books");
            System.out.println(" 11. View Complete Borrowing History");
            System.out.println("\n--- Reservations & Fines ---");
            System.out.println(" 12. Manage Book Reservations");
            System.out.println(" 13. View Fine Records & Collect Payments");
            System.out.println(" 14. Generate Statistical & Audit Reports");
            System.out.println("\n--- Account ---");
            System.out.println(" 15. View My Staff Profile");
            System.out.println(" 16. Logout to Main Portal");
            ConsoleFormatter.printDivider();

            int choice = InputHelper.readInt("Enter choice (1-16): ", 1, 16);

            try {
                switch (choice) {
                    case 1 -> addBookFlow();
                    case 2 -> updateBookFlow();
                    case 3 -> deleteBookFlow();
                    case 4 -> displayAllBooks();
                    case 5 -> searchBooksFlow();
                    case 6 -> registerStudentFlow();
                    case 7 -> displayAllStudents();
                    case 8 -> issueBookFlow();
                    case 9 -> returnBookFlow();
                    case 10 -> displayCurrentlyIssuedBooks();
                    case 11 -> displayBorrowingHistoryFlow();
                    case 12 -> manageReservationsFlow();
                    case 13 -> fineManagementFlow();
                    case 14 -> generateReportsFlow();
                    case 15 -> {
                        librarian.displayProfile();
                        InputHelper.pressEnterToContinue();
                    }
                    case 16 -> {
                        active = false;
                        ConsoleFormatter.printInfo("Librarian logged out successfully.");
                    }
                }
                store.save(concreteService); // Auto-save after successful transactions
            } catch (LibraryException ex) {
                ConsoleFormatter.printError("Operation Failed: " + ex.getMessage());
                InputHelper.pressEnterToContinue();
            }
        }
    }

    private void addBookFlow() throws LibraryException {
        ConsoleFormatter.printSubHeader("ADD NEW BOOK TO CATALOG");
        String id = InputHelper.readLine("Enter Unique Book ID (e.g. B107): ");
        String title = InputHelper.readLine("Enter Book Title: ");
        String author = InputHelper.readLine("Enter Author Name: ");
        String category = InputHelper.readLine("Enter Category (e.g. Computer Science, Mechanical): ");
        String shelf = InputHelper.readLine("Enter Shelf Location (e.g. Rack-CS-3): ");

        ConsoleFormatter.printRfidPrompt("Program RFID Transponder chip for this book.");
        String suggestedTag = RFIDScanner.generateTag(id);
        System.out.println("Suggested Tag UID: " + suggestedTag);
        String rfid = InputHelper.readLine("Enter/Confirm RFID Tag UID: ");
        if (rfid.isEmpty()) rfid = suggestedTag;

        Book newBook = new Book(id, title, author, category, rfid, shelf);
        library.addBook(newBook);
        ConsoleFormatter.printSuccess("Book successfully cataloged and mapped to RFID tag: [" + rfid + "].");
        InputHelper.pressEnterToContinue();
    }

    private void updateBookFlow() throws LibraryException {
        ConsoleFormatter.printSubHeader("UPDATE BOOK DETAILS");
        String id = InputHelper.readLine("Enter Book ID to update: ");
        Book book = library.findBookById(id);
        if (book == null) {
            ConsoleFormatter.printError("No book found with ID: " + id);
            InputHelper.pressEnterToContinue();
            return;
        }

        System.out.println("Current Title    : " + book.getTitle());
        System.out.println("Current Author   : " + book.getAuthor());
        System.out.println("Current Category : " + book.getCategory());
        System.out.println("Current Shelf    : " + book.getShelfLocation());
        System.out.println("(Leave field blank to keep existing value)");

        String newTitle = InputHelper.readLine("New Title: ", true);
        String newAuthor = InputHelper.readLine("New Author: ", true);
        String newCategory = InputHelper.readLine("New Category: ", true);
        String newShelf = InputHelper.readLine("New Shelf Location: ", true);

        library.updateBook(id, newTitle, newAuthor, newCategory, newShelf);
        ConsoleFormatter.printSuccess("Book ID '" + id + "' updated successfully.");
        InputHelper.pressEnterToContinue();
    }

    private void deleteBookFlow() throws LibraryException {
        ConsoleFormatter.printSubHeader("DELETE BOOK FROM CATALOG");
        String id = InputHelper.readLine("Enter Book ID to delete: ");
        Book book = library.findBookById(id);
        if (book == null) {
            ConsoleFormatter.printError("Book ID not found.");
            InputHelper.pressEnterToContinue();
            return;
        }

        System.out.println("Book to delete: " + book.getTitle() + " by " + book.getAuthor());
        boolean confirm = InputHelper.readConfirmation("Are you sure you want to permanently delete this book?");
        if (confirm) {
            library.deleteBook(id);
            ConsoleFormatter.printSuccess("Book '" + id + "' has been removed from catalog.");
        } else {
            ConsoleFormatter.printInfo("Deletion cancelled.");
        }
        InputHelper.pressEnterToContinue();
    }

    private void displayAllBooks() {
        ConsoleFormatter.printSubHeader("LIBRARY BOOK CATALOG");
        List<Book> books = library.getBooks();
        if (books.isEmpty()) {
            System.out.println("The library catalog currently has no books.");
        } else {
            System.out.printf("%-8s | %-26s | %-18s | %-15s | %-12s | %-9s%n",
                    "BOOK ID", "TITLE", "AUTHOR", "CATEGORY", "RFID TAG", "STATUS");
            ConsoleFormatter.printDivider();
            for (Book b : books) {
                System.out.println(b);
            }
            System.out.println("\nTotal Titles in Library: " + books.size());
        }
        InputHelper.pressEnterToContinue();
    }

    private void searchBooksFlow() {
        ConsoleFormatter.printSubHeader("SEARCH BOOK CATALOG");
        System.out.println("  1. General Search (Title, Author, Category, ID)");
        System.out.println("  2. Filter by Category & Keyword");
        int mode = InputHelper.readInt("Select search mode (1-2): ", 1, 2);

        List<Book> results;
        if (mode == 1) {
            String query = InputHelper.readLine("Enter search keyword: ");
            results = library.searchBooks(query);
        } else {
            String category = InputHelper.readLine("Enter Category name: ");
            String query = InputHelper.readLine("Enter Title or Author keyword (optional): ", true);
            results = library.searchBooks(query, category);
        }

        if (results.isEmpty()) {
            ConsoleFormatter.printWarning("No books matched your search criteria.");
        } else {
            ConsoleFormatter.printSuccess("Found " + results.size() + " matching book(s):");
            System.out.printf("%-8s | %-26s | %-18s | %-15s | %-12s | %-9s%n",
                    "BOOK ID", "TITLE", "AUTHOR", "CATEGORY", "RFID TAG", "STATUS");
            ConsoleFormatter.printDivider();
            for (Book b : results) {
                System.out.println(b);
            }
        }
        InputHelper.pressEnterToContinue();
    }

    private void registerStudentFlow() throws LibraryException {
        ConsoleFormatter.printSubHeader("REGISTER NEW COLLEGE STUDENT");
        String id = InputHelper.readLine("Enter Unique Student ID (e.g. STU104): ");
        String name = InputHelper.readLine("Enter Student Full Name: ");
        String email = InputHelper.readLine("Enter College Email: ");
        String password = InputHelper.readPassword("Set Student Portal Password: ");
        String department = InputHelper.readLine("Enter Academic Department (e.g. Computer Science): ");
        String phone = InputHelper.readLine("Enter Contact Number: ");
        int maxBooks = InputHelper.readInt("Enter Maximum Books Allowed to Borrow (Default 3): ");

        Student student = new Student(id, name, email, password, department, phone, maxBooks);
        library.registerStudent(student);
        ConsoleFormatter.printSuccess("Student registered successfully! Student can now log in using ID: " + id);
        InputHelper.pressEnterToContinue();
    }

    private void displayAllStudents() {
        ConsoleFormatter.printSubHeader("REGISTERED STUDENTS DIRECTORY");
        List<Student> students = library.getStudents();
        if (students.isEmpty()) {
            System.out.println("No students registered yet.");
        } else {
            System.out.printf("%-10s | %-20s | %-22s | %-15s%n",
                    "STUDENT ID", "STUDENT NAME", "EMAIL ADDRESS", "DEPARTMENT");
            ConsoleFormatter.printDivider();
            for (Student s : students) {
                System.out.println(s);
            }
            System.out.println("\nTotal Registered Students: " + students.size());
        }
        InputHelper.pressEnterToContinue();
    }

    private void issueBookFlow() throws LibraryException {
        ConsoleFormatter.printSubHeader("ISSUE BOOK (RFID ASSISTED)");
        String studentId = InputHelper.readLine("Enter Student ID: ");
        Student student = library.findStudent(studentId);
        if (student == null) {
            ConsoleFormatter.printError("Student ID '" + studentId + "' is not registered.");
            InputHelper.pressEnterToContinue();
            return;
        }

        System.out.println("Student Verified: " + student.getName() + " (" + student.getDepartment() + ")");

        // Simulated RFID scan
        String rfid = RFIDScanner.scan("Scan book RFID tag to issue: ");
        IssueRecord record = library.issueBook(studentId, rfid);

        ConsoleFormatter.printSuccess("BOOK ISSUED SUCCESSFULLY!");
        System.out.println("Transaction ID : " + record.getRecordId());
        System.out.println("Book Title     : " + record.getBookTitle() + " (ID: " + record.getBookId() + ")");
        System.out.println("Issued Date    : " + record.getIssueDate());
        System.out.println("Due Date       : " + record.getDueDate() + " (Period: " + FineService.STANDARD_BORROW_DAYS + " days)");
        System.out.println("Fine Policy    : Rs. " + FineService.FINE_PER_DAY + " per day for overdue returns.");
        InputHelper.pressEnterToContinue();
    }

    private void returnBookFlow() throws LibraryException {
        ConsoleFormatter.printSubHeader("ACCEPT RETURNED BOOK (RFID ASSISTED)");
        String studentId = InputHelper.readLine("Enter Student ID: ");
        String rfid = RFIDScanner.scan("Scan returned book RFID tag: ");

        double fine = library.returnBook(studentId, rfid);

        ConsoleFormatter.printSuccess("Book returned successfully to library stock!");
        if (fine > 0) {
            ConsoleFormatter.printWarning(String.format("Overdue fine assessed: Rs. %.2f", fine));
        } else {
            ConsoleFormatter.printInfo("Returned on or before due date. No overdue fine assessed.");
        }
        InputHelper.pressEnterToContinue();
    }

    private void displayCurrentlyIssuedBooks() {
        ConsoleFormatter.printSubHeader("CURRENTLY ISSUED BOOKS IN CIRCULATION");
        List<IssueRecord> active = library.getCurrentlyIssuedRecords();
        if (active.isEmpty()) {
            System.out.println("No books are currently in circulation.");
        } else {
            System.out.printf("%-10s | %-10s | %-8s | %-11s | %-11s | %-11s | %-8s | %-10s%n",
                    "TXN ID", "STUDENT", "BOOK ID", "ISSUE DATE", "DUE DATE", "RETURN DATE", "STATUS", "FINE");
            ConsoleFormatter.printDivider();
            for (IssueRecord r : active) {
                System.out.println(r);
            }
            System.out.println("\nActive Loans Count: " + active.size());
        }
        InputHelper.pressEnterToContinue();
    }

    private void displayBorrowingHistoryFlow() {
        ConsoleFormatter.printSubHeader("CIRCULATION BORROWING HISTORY");
        System.out.println("  1. View All Circulation History");
        System.out.println("  2. Filter by Specific Student ID");
        int option = InputHelper.readInt("Select option (1-2): ", 1, 2);

        List<IssueRecord> records;
        if (option == 1) {
            records = library.getAllIssueRecords();
        } else {
            String studentId = InputHelper.readLine("Enter Student ID: ");
            records = library.getHistory(studentId);
        }

        if (records.isEmpty()) {
            System.out.println("No borrowing history records found.");
        } else {
            System.out.printf("%-10s | %-10s | %-8s | %-11s | %-11s | %-11s | %-8s | %-10s%n",
                    "TXN ID", "STUDENT", "BOOK ID", "ISSUE DATE", "DUE DATE", "RETURN DATE", "STATUS", "FINE");
            ConsoleFormatter.printDivider();
            for (IssueRecord r : records) {
                System.out.println(r);
            }
        }
        InputHelper.pressEnterToContinue();
    }

    private void manageReservationsFlow() throws LibraryException {
        ConsoleFormatter.printSubHeader("BOOK RESERVATION QUEUE MANAGEMENT");
        List<Reservation> reservations = library.getReservations();
        if (reservations.isEmpty()) {
            System.out.println("There are no book reservation requests.");
        } else {
            System.out.printf("%-10s | %-10s | %-8s | %-12s | %-10s%n",
                    "RES ID", "STUDENT", "BOOK ID", "DATE", "STATUS");
            ConsoleFormatter.printDivider();
            for (Reservation r : reservations) {
                System.out.println(r);
            }

            System.out.println("\nActions: 1. Cancel a Reservation | 2. Back");
            int act = InputHelper.readInt("Select action (1-2): ", 1, 2);
            if (act == 1) {
                String resId = InputHelper.readLine("Enter Reservation ID to cancel: ");
                library.cancelReservation(resId, null);
                ConsoleFormatter.printSuccess("Reservation " + resId + " has been cancelled.");
            }
        }
        InputHelper.pressEnterToContinue();
    }

    private void fineManagementFlow() throws LibraryException {
        ConsoleFormatter.printSubHeader("FINE MANAGEMENT & FEE COLLECTION");
        List<FineRecord> fines = library.getFineRecords();
        if (fines.isEmpty()) {
            System.out.println("No fine records recorded yet. All accounts clear.");
        } else {
            System.out.printf("%-10s | %-10s | %-8s | %-11s | %-11s | %-8s | %-11s%n",
                    "FINE ID", "STUDENT", "BOOK ID", "AMOUNT", "ASSESSED", "STATUS", "PAID DATE");
            ConsoleFormatter.printDivider();
            for (FineRecord f : fines) {
                System.out.println(f);
            }

            System.out.println("\nOptions: 1. Settle/Collect Student Fine | 2. Back");
            int opt = InputHelper.readInt("Select (1-2): ", 1, 2);
            if (opt == 1) {
                String studentId = InputHelper.readLine("Enter Student ID to collect fine: ");
                double pending = library.getStudentPendingFineTotal(studentId);
                if (pending <= 0) {
                    ConsoleFormatter.printInfo("This student has zero pending fines.");
                } else {
                    System.out.printf("Total Outstanding Fine: Rs. %.2f%n", pending);
                    boolean pay = InputHelper.readConfirmation("Confirm receipt of fine payment?");
                    if (pay) {
                        library.payStudentFine(studentId);
                        ConsoleFormatter.printSuccess("Fine marked as PAID. Receipt acknowledged.");
                    }
                }
            }
        }
        InputHelper.pressEnterToContinue();
    }

    private void generateReportsFlow() {
        ConsoleFormatter.printSubHeader("LIBRARY ANALYTICS & AUDIT REPORTS");
        System.out.println("  1. Executive Summary & Inventory Health");
        System.out.println("  2. Overdue Borrowing Audit");
        System.out.println("  3. Book Category Inventory Breakdown");
        System.out.println("  4. Return to Librarian Menu");
        int rep = InputHelper.readInt("Select report type (1-4): ", 1, 4);

        switch (rep) {
            case 1 -> ReportService.printExecutiveSummary(library.getBooks(), library.getStudents(),
                    library.getAllIssueRecords(), library.getReservations(), library.getFineRecords());
            case 2 -> ReportService.printOverdueReport(library.getAllIssueRecords(), library.getBooks(), library.getStudents());
            case 3 -> ReportService.printCategoryDistribution(library.getBooks());
            case 4 -> { return; }
        }
        InputHelper.pressEnterToContinue();
    }

    // =========================================================================
    // STUDENT PORTAL FLOWS
    // =========================================================================

    private void studentLoginFlow() {
        ConsoleFormatter.printSubHeader("STUDENT SELF-SERVICE PORTAL LOGIN");
        System.out.println("Default Demo Accounts: ID = STU101 (or STU102, STU103) | Password = pass123");

        int attempts = 0;
        Student student = null;

        while (attempts < 3) {
            String studentId = InputHelper.readLine("Enter Student ID: ");
            String password = InputHelper.readPassword("Enter Password: ");

            student = library.authenticateStudent(studentId, password);
            if (student != null) {
                break;
            }
            attempts++;
            ConsoleFormatter.printError("Invalid student credentials! Remaining attempts: " + (3 - attempts));
            if (library.findStudent(studentId) == null) {
                System.out.println("[TIP] Student ID '" + studentId + "' is not registered yet. Please register via Librarian Portal (Menu 1 -> Option 6).");
            } else {
                System.out.println("[TIP] The default password for registered students is: pass123");
            }
        }

        if (student == null) {
            ConsoleFormatter.printWarning("Authentication failed. Returning to main menu.");
            InputHelper.pressEnterToContinue();
            return;
        }

        ConsoleFormatter.printSuccess("Welcome, " + student.getName() + " (" + student.getDepartment() + ")!");
        studentMenu(student);
    }

    private void studentMenu(Student student) {
        boolean active = true;

        while (active) {
            ConsoleFormatter.printHeader("STUDENT DASHBOARD | " + student.getName() + " (" + student.getId() + ")");
            System.out.println("  1. View Available Books For Borrowing");
            System.out.println("  2. Search Book Catalog");
            System.out.println("  3. Self-Service Borrow Book (RFID Scanner)");
            System.out.println("  4. Self-Service Return Book (RFID Scanner)");
            System.out.println("  5. Reserve an Unavailable Book (Join Queue)");
            System.out.println("  6. View My Current Active Loans & Due Dates");
            System.out.println("  7. View My Complete Borrowing History");
            System.out.println("  8. Check My Pending Fines & Settle Dues");
            System.out.println("  9. View My Student Profile");
            System.out.println(" 10. Logout");
            ConsoleFormatter.printDivider();

            int choice = InputHelper.readInt("Enter choice (1-10): ", 1, 10);

            try {
                switch (choice) {
                    case 1 -> displayAvailableBooks();
                    case 2 -> searchBooksFlow();
                    case 3 -> studentBorrowFlow(student);
                    case 4 -> studentReturnFlow(student);
                    case 5 -> studentReserveFlow(student);
                    case 6 -> displayStudentActiveLoans(student);
                    case 7 -> displayStudentHistory(student);
                    case 8 -> displayStudentFines(student);
                    case 9 -> {
                        student.displayProfile();
                        InputHelper.pressEnterToContinue();
                    }
                    case 10 -> {
                        active = false;
                        ConsoleFormatter.printInfo("Logged out of Student Portal.");
                    }
                }
                store.save(concreteService);
            } catch (LibraryException ex) {
                ConsoleFormatter.printError("Operation Failed: " + ex.getMessage());
                InputHelper.pressEnterToContinue();
            }
        }
    }

    private void displayAvailableBooks() {
        ConsoleFormatter.printSubHeader("AVAILABLE BOOKS IN LIBRARY");
        List<Book> books = library.getBooks();
        int count = 0;
        System.out.printf("%-8s | %-26s | %-18s | %-15s | %-12s | %-9s%n",
                "BOOK ID", "TITLE", "AUTHOR", "CATEGORY", "RFID TAG", "STATUS");
        ConsoleFormatter.printDivider();
        for (Book b : books) {
            if (b.isAvailable()) {
                System.out.println(b);
                count++;
            }
        }
        if (count == 0) {
            System.out.println("All books are currently checked out. You can reserve one!");
        } else {
            System.out.println("\nAvailable for borrowing: " + count + " book(s).");
        }
        InputHelper.pressEnterToContinue();
    }

    private void studentBorrowFlow(Student student) throws LibraryException {
        ConsoleFormatter.printSubHeader("SELF-CHECKOUT KIOSK (RFID EMULATION)");
        System.out.println("Student ID: " + student.getId() + " (" + student.getName() + ")");
        String rfid = RFIDScanner.scan("Scan book RFID transponder on self-checkout kiosk: ");

        IssueRecord record = library.issueBook(student.getId(), rfid);
        ConsoleFormatter.printSuccess("SELF-CHECKOUT COMPLETE!");
        System.out.println("Book Title   : " + record.getBookTitle() + " (ID: " + record.getBookId() + ")");
        System.out.println("Borrow Date  : " + record.getIssueDate());
        System.out.println("Due Date     : " + record.getDueDate() + " (Please return within 7 days)");
        InputHelper.pressEnterToContinue();
    }

    private void studentReturnFlow(Student student) throws LibraryException {
        ConsoleFormatter.printSubHeader("SELF-SERVICE RETURN DROPBOX (RFID EMULATION)");
        String rfid = RFIDScanner.scan("Scan book RFID transponder at return drop box: ");

        double fine = library.returnBook(student.getId(), rfid);
        ConsoleFormatter.printSuccess("Book returned successfully to the library drop box!");
        if (fine > 0) {
            ConsoleFormatter.printWarning(String.format("Overdue fine of Rs. %.2f has been billed to your account.", fine));
        } else {
            ConsoleFormatter.printInfo("Returned on time. No overdue penalty.");
        }
        InputHelper.pressEnterToContinue();
    }

    private void studentReserveFlow(Student student) throws LibraryException {
        ConsoleFormatter.printSubHeader("RESERVE A BOOK (FIFO WAITING QUEUE)");
        String bookId = InputHelper.readLine("Enter Book ID to reserve (e.g. B101): ");
        Reservation reservation = library.reserveBook(student.getId(), bookId);

        int pos = library.getReservationPosition(reservation);
        ConsoleFormatter.printSuccess("Reservation placed successfully!");
        System.out.println("Reservation ID : " + reservation.getReservationId());
        System.out.println("Book Catalog ID: " + reservation.getBookId());
        System.out.println("Current Status : " + reservation.getStatus());
        if (pos == 0) {
            System.out.println("Notice: The book is currently AVAILABLE on shelf! Ready for immediate pickup.");
        } else {
            System.out.println("Queue Position : #" + pos + " in line. You will be prioritized upon return.");
        }
        InputHelper.pressEnterToContinue();
    }

    private void displayStudentActiveLoans(Student student) {
        ConsoleFormatter.printSubHeader("MY CURRENTLY BORROWED BOOKS");
        List<IssueRecord> history = library.getHistory(student.getId());
        int activeCount = 0;

        System.out.printf("%-10s | %-8s | %-11s | %-11s | %-10s | %-10s%n",
                "TXN ID", "BOOK ID", "ISSUE DATE", "DUE DATE", "STATUS", "OVERDUE DAYS");
        ConsoleFormatter.printDivider();
        for (IssueRecord r : history) {
            if (!r.isReturned()) {
                activeCount++;
                String status = r.isOverdue() ? "OVERDUE!" : "ACTIVE";
                System.out.printf("%-10s | %-8s | %-11s | %-11s | %-10s | %-10s%n",
                        r.getRecordId(), r.getBookId(), r.getIssueDate(), r.getDueDate(),
                        status, r.getDaysOverdue() > 0 ? (r.getDaysOverdue() + " days") : "On Track");
            }
        }

        if (activeCount == 0) {
            System.out.println("You have no currently borrowed books.");
        } else {
            System.out.println("\nTotal Active Loans: " + activeCount + " / " + student.getMaxBooksAllowed());
        }
        InputHelper.pressEnterToContinue();
    }

    private void displayStudentHistory(Student student) {
        ConsoleFormatter.printSubHeader("MY COMPLETE BORROWING HISTORY");
        List<IssueRecord> history = library.getHistory(student.getId());
        if (history.isEmpty()) {
            System.out.println("You have not borrowed any books yet.");
        } else {
            System.out.printf("%-10s | %-10s | %-8s | %-11s | %-11s | %-11s | %-8s | %-10s%n",
                    "TXN ID", "STUDENT", "BOOK ID", "ISSUE DATE", "DUE DATE", "RETURN DATE", "STATUS", "FINE");
            ConsoleFormatter.printDivider();
            for (IssueRecord r : history) {
                System.out.println(r);
            }
        }
        InputHelper.pressEnterToContinue();
    }

    private void displayStudentFines(Student student) throws LibraryException {
        ConsoleFormatter.printSubHeader("MY FINES & SETTLEMENTS");
        List<FineRecord> fines = library.getStudentFineRecords(student.getId());
        double pending = library.getStudentPendingFineTotal(student.getId());

        if (fines.isEmpty()) {
            ConsoleFormatter.printInfo("No fines have ever been assessed to your account. Great job!");
        } else {
            System.out.printf("%-10s | %-8s | %-11s | %-11s | %-8s | %-11s%n",
                    "FINE ID", "BOOK ID", "AMOUNT", "ASSESSED", "STATUS", "PAID DATE");
            ConsoleFormatter.printDivider();
            for (FineRecord f : fines) {
                System.out.printf("%-10s | %-8s | Rs. %-7.2f | %-11s | %-8s | %-11s%n",
                        f.getFineId(), f.getBookId(), f.getAmount(), f.getDateAssessed(),
                        (f.isPaid() ? "PAID" : "UNPAID"),
                        (f.getPaymentDate() == null ? "-" : f.getPaymentDate().toString()));
            }

            System.out.printf("\nTotal Outstanding Dues: Rs. %.2f%n", pending);
            if (pending > 0) {
                boolean pay = InputHelper.readConfirmation("Would you like to pay your pending fine now?");
                if (pay) {
                    library.payStudentFine(student.getId());
                    ConsoleFormatter.printSuccess("All pending dues cleared! Thank you.");
                }
            }
        }
        InputHelper.pressEnterToContinue();
    }

    // =========================================================================
    // GUEST ACCESS FLOW
    // =========================================================================

    private void guestBrowseFlow() {
        boolean back = false;
        while (!back) {
            ConsoleFormatter.printHeader("OPEN CATALOG BROWSER (GUEST / PUBLIC ACCESS)");
            System.out.println("  1. View All Books In Catalog");
            System.out.println("  2. Search Books by Title, Author, Category or ID");
            System.out.println("  3. View Available Books For Lending");
            System.out.println("  4. Return to Main Menu");
            ConsoleFormatter.printDivider();

            int choice = InputHelper.readInt("Select option (1-4): ", 1, 4);
            switch (choice) {
                case 1 -> displayAllBooks();
                case 2 -> searchBooksFlow();
                case 3 -> displayAvailableBooks();
                case 4 -> back = true;
            }
        }
    }

    // =========================================================================
    // VIVA & OOP ARCHITECTURE GUIDE
    // =========================================================================

    private void displayVivaGuide() {
        ConsoleFormatter.printHeader("NEXLIB TECHNICAL ARCHITECTURE & VIVA DEMONSTRATION GUIDE");
        System.out.println("""
1. OOP CONCEPTS DEMONSTRATED IN SOURCE CODE:
   * Classes and Objects : Book, Student, Librarian, IssueRecord, Reservation, FineRecord.
   * Encapsulation       : All model fields are private/protected with validated getters/setters.
   * Inheritance         : Abstract base 'User' inherited by 'Student' and 'Librarian'.
   * Abstraction         : Abstract class 'User' and 'ILibraryService' interface defining contracts.
   * Polymorphism        : Dynamic method dispatch in displayProfile(), getRole(), and ILibraryService.
   * Method Overloading  :
       - LibraryService.searchBooks(query) and searchBooks(query, category).
       - FineService.calculate(dueDate, returnDate) and calculate(dueDate, returnDate, customRate).
       - Constructor overloading across Book, Student, Librarian, and IssueRecord.
   * Method Overriding   : toString() and displayProfile() overridden across entity classes.
   * Static Members      : FineService.FINE_PER_DAY, STANDARD_BORROW_DAYS, utility classes.

2. COLLECTIONS FRAMEWORK IMPLEMENTATION:
   * ArrayList           : Dynamic lists for books, registered students, and issue transactions.
   * HashMap             : O(1) lookup table mapping unique RFID transponder tags to Book objects.
   * Queue & LinkedList  : First-In-First-Out (FIFO) waiting list for book reservations.

3. CUSTOM EXCEPTION HANDLING HIERARCHY:
   * LibraryException (Base checked exception)
     |-- BookNotFoundException
     |-- BookUnavailableException
     |-- InvalidRFIDException
     |-- StudentNotFoundException
     |-- DuplicateBookException
     |-- InvalidInputException

4. HARDWARE SIMULATION:
   * RFIDScanner emulates 13.56 MHz RFID readers via console input with verification and auto-tagging.

5. PERSISTENCE:
   * Java Object Serialization (DataStore) with atomic file backup (.bak) and auto-index rebuilding.
""");
        ConsoleFormatter.printDivider();
        InputHelper.pressEnterToContinue();
    }
}
