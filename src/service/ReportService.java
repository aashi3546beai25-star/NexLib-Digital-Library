package service;

import model.Book;
import model.IssueRecord;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Statistical audit reports using Collections and Streams.
 */
public class ReportService {
    public static void printSummary(List<Book> books, List<IssueRecord> loans, int students, int alerts) {
        System.out.println("\n============================================================================");
        System.out.println("                 NEXLIB EXECUTIVE AUDIT REPORT");
        System.out.println("============================================================================");
        long avail = books.stream().filter(Book::isAvailable).count();
        long activeLoans = loans.stream().filter(r -> !r.isReturned()).count();
        double fines = loans.stream().mapToDouble(IssueRecord::getFine).sum();

        System.out.println("Total Catalog Titles     : " + books.size());
        System.out.println("Available on Shelf       : " + avail);
        System.out.println("Currently Borrowed       : " + activeLoans);
        System.out.println("Registered Students      : " + students);
        System.out.println("Active Overdue Alerts    : " + alerts);
        System.out.printf("Total Fines Assessed     : Rs. %.2f%n", fines);
        System.out.println("----------------------------------------------------------------------------");
    }
}
