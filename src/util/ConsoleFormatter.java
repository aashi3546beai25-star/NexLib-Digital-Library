package util;

import model.Book;
import model.IssueRecord;
import model.Student;
import java.util.List;

/**
 * Uniform ASCII table layout formatter.
 */
public class ConsoleFormatter {
    private ConsoleFormatter() {}

    public static void printHeader(String t) {
        System.out.println("\n============================================================================");
        System.out.println("  " + t);
        System.out.println("============================================================================");
    }

    public static void printSubHeader(String t) {
        System.out.println("\n----------------------------------------------------------------------------");
        System.out.println("  " + t);
        System.out.println("----------------------------------------------------------------------------");
    }

    public static void printDivider() {
        System.out.println("----------------------------------------------------------------------------");
    }

    public static void printBookTable(List<Book> list) {
        if (list == null || list.isEmpty()) { System.out.println("No books found."); return; }
        System.out.printf("%-8s | %-26s | %-18s | %-15s | %-10s | %-9s%n", "BOOK ID", "TITLE", "AUTHOR", "CATEGORY", "SHELF", "STATUS");
        printDivider();
        list.forEach(System.out::println);
        System.out.println("Total: " + list.size() + " books");
    }

    public static void printIssueTable(List<IssueRecord> list) {
        if (list == null || list.isEmpty()) { System.out.println("No records found."); return; }
        System.out.printf("%-10s | %-8s | %-16s | %-8s | %-10s | %-10s | %-8s | %-8s%n",
                "TXN ID", "STUDENT", "NAME", "BOOK ID", "ISSUE DATE", "DUE DATE", "STATUS", "FINE");
        printDivider();
        list.forEach(System.out::println);
    }

    public static void printStudentTable(List<Student> list) {
        if (list == null || list.isEmpty()) { System.out.println("No students found."); return; }
        System.out.printf("%-10s | %-20s | %-20s%n", "ID", "NAME", "DEPARTMENT");
        printDivider();
        list.forEach(s -> System.out.printf("%-10s | %-20s | %-20s%n", s.getId(), s.getName(), s.getDepartment()));
    }
}
