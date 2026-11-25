package app;

import exception.LibraryException;
import model.Book;
import model.IssueRecord;
import model.Librarian;
import model.Student;
import service.LibraryService;
import storage.DataStore;
import util.ConsoleFormatter;
import util.InputHelper;
import util.RFIDScanner;

import java.util.List;

/**
 * Controller for the Librarian administrative portal (6 core options).
 */
public class LibrarianPortal {
    private final LibraryService library;
    private final DataStore store;

    public LibrarianPortal(LibraryService library, DataStore store) {
        this.library = library;
        this.store = store;
    }

    public void login() {
        ConsoleFormatter.printSubHeader("LIBRARIAN SECURE LOGIN");
        System.out.println("(Default Credentials: Username = admin | Password = admin123)");

        for (int attempts = 1; attempts <= 3; attempts++) {
            String user = InputHelper.readLine("Enter Librarian Username: ");
            String pass = InputHelper.readPassword("Enter Password: ");

            Librarian librarian = library.authenticateLibrarian(user, pass);
            if (librarian != null) {
                System.out.println("\n[SUCCESS] Welcome, " + librarian.getName() + " (" + librarian.getDesignation() + ")!");
                menu(librarian);
                return;
            }
            System.out.println("\n[ERROR] Invalid credentials! Remaining attempts: " + (3 - attempts));
        }
        System.out.println("\n[WARNING] Maximum login attempts exceeded. Returning to main menu.");
        InputHelper.pressEnterToContinue();
    }

    public void menu(Librarian librarian) {
        boolean active = true;
        while (active) {
            ConsoleFormatter.printHeader("LIBRARIAN CONSOLE | Staff: " + librarian.getName());
            System.out.println(" 1. Add Book (With RFID)");
            System.out.println(" 2. View All Books In Catalog");
            System.out.println(" 3. Add Student (Register)");
            System.out.println(" 4. Issue Book (RFID Scan)");
            System.out.println(" 5. Return Book (RFID Scan & Fine Check)");
            System.out.println(" 6. Logout");
            ConsoleFormatter.printDivider();

            int choice = InputHelper.readInt("Enter choice (1-6): ", 1, 6);
            try {
                switch (choice) {
                    case 1 -> addBook();
                    case 2 -> { ConsoleFormatter.printBookTable(library.getBooks()); InputHelper.pressEnterToContinue(); }
                    case 3 -> studentFlow();
                    case 4 -> issueBookFlow(null);
                    case 5 -> returnBookFlow(null);
                    case 6 -> { active = false; System.out.println("\n[INFO] Librarian logged out."); }
                }
                store.save(library);
            } catch (LibraryException ex) {
                System.out.println("\n[ERROR] Operation Failed: " + ex.getMessage());
                InputHelper.pressEnterToContinue();
            }
        }
    }

    private void addBook() throws LibraryException {
        ConsoleFormatter.printSubHeader("ADD NEW BOOK TO CATALOG");
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

    private void studentFlow() throws LibraryException {
        ConsoleFormatter.printSubHeader("STUDENT MANAGEMENT");
        System.out.println("1. Add New Student | 2. View All Registered Students");
        int opt = InputHelper.readInt("Select (1-2): ", 1, 2);
        if (opt == 1) {
            registerStudent();
        } else {
            displayStudents();
        }
    }

    private void registerStudent() throws LibraryException {
        ConsoleFormatter.printSubHeader("ADD NEW STUDENT");
        String id = InputHelper.readLine("Enter Student ID (e.g. STU104): ");
        String name = InputHelper.readLine("Enter Student Name: ");
        String dept = InputHelper.readLine("Enter Department: ");

        library.registerStudent(new Student(id, name, dept));
        System.out.println("\n[SUCCESS] Student added successfully! Can now log in using ID: " + id + " (Password: pass123)");
        InputHelper.pressEnterToContinue();
    }

    private void displayStudents() {
        ConsoleFormatter.printSubHeader("REGISTERED STUDENTS DIRECTORY");
        List<Student> list = library.getStudents();
        ConsoleFormatter.printStudentTable(list);
        InputHelper.pressEnterToContinue();
    }

    public void issueBookFlow(String studentId) throws LibraryException {
        ConsoleFormatter.printSubHeader("ISSUE BOOK (RFID ASSISTED)");
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

    public void returnBookFlow(String studentId) throws LibraryException {
        ConsoleFormatter.printSubHeader("RETURN BOOK (RFID ASSISTED)");
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
}
