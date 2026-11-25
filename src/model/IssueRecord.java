package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Tracks the lending transaction of a book to a registered student.
 * Demonstrates state management, date manipulation, and encapsulation.
 */
public class IssueRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String recordId;
    private final String studentId;
    private String studentName;
    private final String bookId;
    private String bookTitle;
    private final LocalDate issueDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;
    private double fine;

    /**
     * Primary constructor with explicit student name and book details.
     */
    public IssueRecord(String recordId, String studentId, String studentName, String bookId,
                       String bookTitle, LocalDate issueDate, LocalDate dueDate) {
        this.recordId = recordId;
        this.studentId = studentId;
        this.studentName = (studentName != null && !studentName.trim().isEmpty()) ? studentName.trim() : "N/A";
        this.bookId = bookId;
        this.bookTitle = bookTitle != null ? bookTitle : "N/A";
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = null;
        this.fine = 0.0;
    }

    /**
     * Overloaded constructor without studentName for backwards compatibility.
     */
    public IssueRecord(String recordId, String studentId, String bookId,
                       String bookTitle, LocalDate issueDate, LocalDate dueDate) {
        this(recordId, studentId, "N/A", bookId, bookTitle, issueDate, dueDate);
    }

    /**
     * Backwards-compatible constructor for existing project signatures.
     */
    public IssueRecord(String studentId, String bookId, LocalDate issueDate, LocalDate dueDate) {
        this("TXN-" + System.currentTimeMillis() % 100000, studentId, "N/A", bookId, "N/A", issueDate, dueDate);
    }

    public String getRecordId() {
        return recordId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return (studentName != null && !studentName.isEmpty()) ? studentName : "N/A";
    }

    public void setStudentName(String studentName) {
        if (studentName != null && !studentName.trim().isEmpty()) {
            this.studentName = studentName.trim();
        }
    }

    public String getBookId() {
        return bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public double getFine() {
        return fine;
    }

    public boolean isReturned() {
        return returnDate != null;
    }

    public boolean isOverdue() {
        if (isReturned()) {
            return returnDate.isAfter(dueDate);
        }
        return LocalDate.now().isAfter(dueDate);
    }

    public long getDaysOverdue() {
        LocalDate compareDate = isReturned() ? returnDate : LocalDate.now();
        long days = ChronoUnit.DAYS.between(dueDate, compareDate);
        return days > 0 ? days : 0;
    }

    public void completeReturn(LocalDate date, double fineAmount) {
        this.returnDate = date;
        this.fine = fineAmount >= 0 ? fineAmount : 0.0;
    }

    @Override
    public String toString() {
        String status = isReturned() ? "RETURNED" : (isOverdue() ? "OVERDUE" : "ACTIVE");
        return String.format("%-10s | %-10s | %-16s | %-8s | %-11s | %-11s | %-11s | %-8s | Rs. %-6.2f",
                recordId, studentId, getStudentName(), bookId, issueDate, dueDate,
                (returnDate == null ? "Pending" : returnDate.toString()),
                status, fine);
    }
}
