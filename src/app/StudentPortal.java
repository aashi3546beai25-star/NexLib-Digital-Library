package app;

import model.Book;
import model.IssueRecord;
import model.Reservation;
import model.Student;
import service.LibraryService;
import storage.DataStore;
import util.ConsoleFormatter;
import util.InputHelper;

/**
 * Self-service circulation portal for students.
 */
public class StudentPortal {
    private final LibraryService svc;
    private final DataStore store;

    public StudentPortal(LibraryService s, DataStore st) { this.svc = s; this.store = st; }

    public void login() {
        ConsoleFormatter.printHeader("STUDENT PORTAL LOGIN");
        System.out.println("TIP: Default accounts STU101, STU102, STU103, Password pass123");
        for (int i = 0; i < 3; i++) {
            String u = InputHelper.readLine("Student ID: ");
            String p = InputHelper.readLine("Password: ");
            Student stu = svc.authStudent(u, p);
            if (stu != null) { menu(stu); return; }
            System.out.println("ERROR: Invalid credentials. Attempts left: " + (2 - i));
        }
    }

    private void menu(Student stu) {
        boolean active = true;
        while (active) {
            ConsoleFormatter.printHeader("STUDENT DASHBOARD | " + stu.getName() + " - " + stu.getId());
            System.out.println(" 1. View Available Books");
            System.out.println(" 2. Search Books");
            System.out.println(" 3. Borrow Book");
            System.out.println(" 4. Return Book");
            System.out.println(" 5. Reserve Book");
            System.out.println(" 6. View My Active Loans & History");
            System.out.println(" 7. Check Fines & Pay");
            System.out.println(" 8. Profile");
            System.out.println(" 9. Logout");
            ConsoleFormatter.printDivider();

            int ch = InputHelper.readInt("Enter choice: ", 1, 9);
            try {
                switch (ch) {
                    case 1 -> ConsoleFormatter.printBookTable(svc.getAvailableBooksStream());
                    case 2 -> {
                        String q = InputHelper.readLine("Keyword: ");
                        String cat = InputHelper.readLine("Category: ");
                        ConsoleFormatter.printBookTable(svc.searchBooks(q, cat));
                    }
                    case 3 -> {
                        String bid = InputHelper.readLine("Enter Book ID: ");
                        IssueRecord r = svc.issueBook(stu.getId(), bid);
                        System.out.println("SUCCESS: Borrowed! Due: " + r.getDueDate());
                    }
                    case 4 -> {
                        String bid = InputHelper.readLine("Enter Book ID: ");
                        double f = svc.returnBook(stu.getId(), bid);
                        System.out.println("SUCCESS: Returned! Fine: Rs. " + f);
                    }
                    case 5 -> {
                        String bid = InputHelper.readLine("Enter Book ID to reserve: ");
                        Reservation res = svc.reserveBook(stu.getId(), bid);
                        System.out.println("SUCCESS: Reserved! ID: " + res.getResId() + " | Status: " + res.getStatus());
                    }
                    case 6 -> ConsoleFormatter.printIssueTable(svc.getHistory(stu.getId()));
                    case 7 -> {
                        double dues = svc.getStudentPendingFineTotal(stu.getId());
                        System.out.printf("Total Outstanding Fines: Rs. %.2f%n", dues);
                        if (dues > 0 && InputHelper.readConfirmation("Pay now?")) {
                            svc.payStudentFine(stu.getId());
                            System.out.println("SUCCESS: Fines cleared!");
                        }
                    }
                    case 8 -> stu.displayProfile();
                    case 9 -> active = false;
                }
                store.save(svc);
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
            InputHelper.pressEnterToContinue();
        }
    }
}
