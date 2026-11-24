package service;

import model.Book;
import model.FineRecord;
import model.IssueRecord;
import model.Reservation;
import model.Student;
import util.ConsoleFormatter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service generating administrative analytics, circulation audits, and inventory reports.
 * Demonstrates collection aggregation and reporting logic.
 */
public class ReportService {

    private ReportService() { }

    /**
     * Prints a comprehensive statistical summary of the library.
     */
    public static void printExecutiveSummary(List<Book> books,
                                             List<Student> students,
                                             List<IssueRecord> issueRecords,
                                             List<Reservation> reservations,
                                             List<FineRecord> fineRecords) {
        ConsoleFormatter.printHeader("NEXLIB EXECUTIVE AUDIT & STATISTICAL REPORT");

        int totalBooks = books.size();
        long availableBooks = books.stream().filter(Book::isAvailable).count();
        long issuedBooks = totalBooks - availableBooks;
        int totalStudents = students.size();

        long activeLoans = issueRecords.stream().filter(r -> !r.isReturned()).count();
        long overdueLoans = issueRecords.stream().filter(r -> !r.isReturned() && r.isOverdue()).count();

        long pendingReservations = reservations.stream()
                .filter(r -> "WAITING".equalsIgnoreCase(r.getStatus()) || "AVAILABLE".equalsIgnoreCase(r.getStatus()))
                .count();

        double totalFineAssessed = fineRecords.stream().mapToDouble(FineRecord::getAmount).sum();
        double totalFineCollected = fineRecords.stream().filter(FineRecord::isPaid).mapToDouble(FineRecord::getAmount).sum();
        double outstandingFines = totalFineAssessed - totalFineCollected;

        System.out.printf("| %-35s : %-34d |%n", "Total Catalog Titles", totalBooks);
        System.out.printf("| %-35s : %-34d |%n", "Books Currently Available", availableBooks);
        System.out.printf("| %-35s : %-34d |%n", "Books In Circulation (Issued)", issuedBooks);
        System.out.printf("| %-35s : %-34d |%n", "Total Registered Students", totalStudents);
        System.out.printf("| %-35s : %-34d |%n", "Active Issue Loans", activeLoans);
        System.out.printf("| %-35s : %-34d |%n", "Overdue Loans Alert", overdueLoans);
        System.out.printf("| %-35s : %-34d |%n", "Active Reservation Requests", pendingReservations);
        System.out.printf("| %-35s : Rs. %-30.2f |%n", "Total Fines Assessed", totalFineAssessed);
        System.out.printf("| %-35s : Rs. %-30.2f |%n", "Total Fines Collected", totalFineCollected);
        System.out.printf("| %-35s : Rs. %-30.2f |%n", "Outstanding Pending Balance", outstandingFines);
        ConsoleFormatter.printDivider();
    }

    /**
     * Prints an audit of all currently overdue books.
     */
    public static void printOverdueReport(List<IssueRecord> issueRecords, List<Book> books, List<Student> students) {
        ConsoleFormatter.printSubHeader("OVERDUE BORROWING AUDIT");

        boolean hasOverdue = false;
        System.out.printf("%-10s | %-12s | %-18s | %-12s | %-11s | %-10s%n",
                "RECORD ID", "STUDENT ID", "STUDENT NAME", "BOOK ID", "DUE DATE", "OVERDUE");
        ConsoleFormatter.printDivider();

        for (IssueRecord record : issueRecords) {
            if (!record.isReturned() && record.isOverdue()) {
                hasOverdue = true;
                String studentName = "Unknown";
                for (Student s : students) {
                    if (s.getId().equalsIgnoreCase(record.getStudentId())) {
                        studentName = s.getName();
                        break;
                    }
                }
                long days = record.getDaysOverdue();
                System.out.printf("%-10s | %-12s | %-18s | %-12s | %-11s | %-10s%n",
                        record.getRecordId(), record.getStudentId(),
                        studentName.length() > 18 ? studentName.substring(0, 15) + "..." : studentName,
                        record.getBookId(), record.getDueDate(), days + " days");
            }
        }

        if (!hasOverdue) {
            System.out.println("No overdue books at present. All active loans are within due date.");
        }
    }

    /**
     * Prints category distribution analysis of books.
     * Demonstrates HashMap usage for frequency counting.
     */
    public static void printCategoryDistribution(List<Book> books) {
        ConsoleFormatter.printSubHeader("BOOK CATEGORY INVENTORY BREAKDOWN");
        Map<String, Integer> categoryCount = new HashMap<>();

        for (Book book : books) {
            String cat = book.getCategory();
            categoryCount.put(cat, categoryCount.getOrDefault(cat, 0) + 1);
        }

        System.out.printf("%-30s | %-15s%n", "CATEGORY", "BOOK COUNT");
        ConsoleFormatter.printDivider();
        for (Map.Entry<String, Integer> entry : categoryCount.entrySet()) {
            System.out.printf("%-30s | %-15d%n", entry.getKey(), entry.getValue());
        }
    }
}
