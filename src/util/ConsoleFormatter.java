package util;

import model.Book;
import model.IssueRecord;
import model.Reservation;
import model.Student;

import java.util.List;

/**
 * Utility for uniform console output, ASCII tables, and section headers.
 */
public class ConsoleFormatter {

    private ConsoleFormatter() { }

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

    public static void printBookTable(List<Book> books) {
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

    public static void printIssueTable(List<IssueRecord> records) {
        if (records == null || records.isEmpty()) {
            System.out.println("No circulation records found.");
            return;
        }
        System.out.printf("%-10s | %-10s | %-16s | %-8s | %-11s | %-11s | %-11s | %-8s | %-10s%n",
                "TXN ID", "STUDENT ID", "STUDENT NAME", "BOOK ID", "ISSUE DATE", "DUE DATE", "RETURN DATE", "STATUS", "FINE");
        printDivider();
        for (IssueRecord r : records) System.out.println(r);
    }

    public static void printStudentTable(List<Student> students) {
        if (students == null || students.isEmpty()) {
            System.out.println("No students registered.");
            return;
        }
        System.out.printf("%-10s | %-20s | %-22s | %-15s%n", "STUDENT ID", "STUDENT NAME", "EMAIL", "DEPARTMENT");
        printDivider();
        for (Student s : students) System.out.println(s);
        System.out.println("Total Registered Students: " + students.size());
    }

    public static void printReservationTable(List<Reservation> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("No reservation requests.");
            return;
        }
        System.out.printf("%-10s | %-10s | %-16s | %-8s | %-12s | %-10s%n",
                "RES ID", "STUDENT ID", "STUDENT NAME", "BOOK ID", "DATE", "STATUS");
        printDivider();
        for (Reservation r : list) System.out.println(r);
    }
}
