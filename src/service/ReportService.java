package service;

import model.Book;
import model.IssueRecord;
import model.Student;
import util.ConsoleFormatter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service providing statistical reporting and audit summaries.
 */
public class ReportService {

    private ReportService() { }

    public static void printExecutiveSummary(List<Book> books, List<Student> students, List<IssueRecord> records) {
        ConsoleFormatter.printSubHeader("EXECUTIVE LIBRARY AUDIT SUMMARY");
        int available = 0;
        int active = 0;
        double fines = 0;
        for (Book b : books) {
            if (b.isAvailable()) available++;
        }
        for (IssueRecord r : records) {
            if (!r.isReturned()) active++;
            fines += r.getFine();
        }
        System.out.println("Total Catalog Titles     : " + books.size());
        System.out.println("Books Available on Shelf : " + available);
        System.out.println("Books Currently Issued   : " + active);
        System.out.println("Total Registered Students: " + students.size());
        System.out.printf("Total Overdue Fines      : Rs. %.2f%n", fines);
        ConsoleFormatter.printDivider();
    }

    public static void printOverdueReport(List<IssueRecord> records) {
        ConsoleFormatter.printSubHeader("OVERDUE BOOKS AUDIT");
        boolean found = false;
        for (IssueRecord r : records) {
            if (!r.isReturned() && r.isOverdue()) {
                found = true;
                System.out.println("Student: " + r.getStudentId() + " (" + r.getStudentName() + ") | Book: " + r.getBookId()
                        + " | Due: " + r.getDueDate() + " | Overdue: " + r.getDaysOverdue() + " days");
            }
        }
        if (!found) {
            System.out.println("No overdue books at present. All loans are on time.");
        }
        ConsoleFormatter.printDivider();
    }

    public static void printCategoryDistribution(List<Book> books) {
        ConsoleFormatter.printSubHeader("BOOK CATEGORY INVENTORY BREAKDOWN");
        Map<String, Integer> counts = new HashMap<>();
        for (Book b : books) {
            String cat = b.getCategory();
            counts.put(cat, counts.getOrDefault(cat, 0) + 1);
        }
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            System.out.printf("%-20s : %d book(s)%n", entry.getKey(), entry.getValue());
        }
        ConsoleFormatter.printDivider();
    }
}
