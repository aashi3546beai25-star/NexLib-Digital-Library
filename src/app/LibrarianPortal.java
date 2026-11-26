package app;

import exception.LibraryException;
import model.*;
import service.LibraryService;
import storage.DataStore;
import util.ConsoleFormatter;
import util.InputHelper;

import java.util.List;

/**
 * Administrative portal for library staff.
 */
public class LibrarianPortal {
    private final LibraryService svc;
    private final DataStore store;

    public LibrarianPortal(LibraryService s, DataStore st) { this.svc = s; this.store = st; }

    public void login() {
        ConsoleFormatter.printHeader("LIBRARIAN ADMINISTRATIVE ACCESS");
        System.out.println("TIP: Default username = admin, password = admin123");
        for (int i = 0; i < 3; i++) {
            String u = InputHelper.readLine("Username: ");
            String p = InputHelper.readLine("Password: ");
            Librarian lib = svc.authLibrarian(u, p);
            if (lib != null) { menu(lib); return; }
            System.out.println("ERROR: Invalid login. Remaining attempts: " + (2 - i));
        }
    }

    private void menu(Librarian lib) {
        boolean active = true;
        while (active) {
            ConsoleFormatter.printHeader("LIBRARIAN CONSOLE | " + lib.getName());
            System.out.println(" 1. Add Book to Catalog");
            System.out.println(" 2. View Books");
            System.out.println(" 3. Register Student / Directory");
            System.out.println(" 4. Issue Book to Student");
            System.out.println(" 5. Accept Return & Assess Fine");
            System.out.println(" 6. Logout");
            ConsoleFormatter.printDivider();

            int ch = InputHelper.readInt("Enter choice: ", 1, 6);
            try {
                switch (ch) {
                    case 1 -> {
                        String id = InputHelper.readLine("Enter Book ID: ");
                        String title = InputHelper.readLine("Enter Title: ");
                        String author = InputHelper.readLine("Enter Author: ");
                        String cat = InputHelper.readLine("Enter Category: ");
                        String shelf = InputHelper.readLine("Enter Shelf Rack: ");
                        svc.addBook(new Book(id, title, author, cat, shelf));
                        System.out.println("SUCCESS: Book added successfully.");
                    }
                    case 2 -> {
                        System.out.println("1. Natural ID | 2. Title | 3. Author | 4. Category | 5. Reverse");
                        int s = InputHelper.readInt("Select: ", 1, 5);
                        List<Book> list = switch (s) {
                            case 1 -> svc.getBooks().stream().sorted().toList();
                            case 2 -> svc.getSortedBooks(Book.BY_TITLE);
                            case 3 -> svc.getSortedBooks(Book.BY_AUTHOR);
                            case 4 -> svc.getSortedBooks(Book.BY_CATEGORY);
                            case 5 -> svc.getBooksReversedWithListIterator();
                            default -> svc.getBooks();
                        };
                        ConsoleFormatter.printBookTable(list);
                    }
                    case 3 -> {
                        System.out.println("1. Register Student | 2. View Directory");
                        if (InputHelper.readInt("Choice: ", 1, 2) == 1) {
                            String sid = InputHelper.readLine("Student ID: ");
                            String name = InputHelper.readLine("Name: ");
                            String dept = InputHelper.readLine("Department: ");
                            svc.registerStudent(new Student(sid, name, dept));
                            System.out.println("SUCCESS: Student registered! Default pass: pass123");
                        } else {
                            ConsoleFormatter.printStudentTable(svc.getStudents());
                        }
                    }
                    case 4 -> {
                        String sid = InputHelper.readLine("Student ID: ");
                        String bid = InputHelper.readLine("Book ID: ");
                        IssueRecord r = svc.issueBook(sid, bid);
                        System.out.println("SUCCESS: Book issued! Txn: " + r.getTxnId() + " | Due: " + r.getDueDate());
                    }
                    case 5 -> {
                        String sid = InputHelper.readLine("Student ID: ");
                        String bid = InputHelper.readLine("Book ID: ");
                        double fine = svc.returnBook(sid, bid);
                        System.out.println("SUCCESS: Book returned! Fine: Rs. " + fine);
                    }
                    case 6 -> active = false;
                }
                store.save(svc);
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
            InputHelper.pressEnterToContinue();
        }
    }
}
