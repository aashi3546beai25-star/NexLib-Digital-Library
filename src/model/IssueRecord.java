package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Lending circulation record tracking loan dates and fines.
 */
public class IssueRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private String txnId, studentId, studentName, bookId, bookTitle;
    private LocalDate issueDate, dueDate, returnDate;
    private double fine = 0.0;

    public IssueRecord(String txnId, String stuId, String stuName, String bookId, String title, LocalDate issue, LocalDate due) {
        this.txnId = txnId; this.studentId = stuId; this.studentName = stuName;
        this.bookId = bookId; this.bookTitle = title; this.issueDate = issue; this.dueDate = due;
    }

    public String getTxnId() { return txnId; }
    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getBookId() { return bookId; }
    public String getBookTitle() { return bookTitle; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public double getFine() { return fine; }
    public boolean isReturned() { return returnDate != null; }
    public boolean isOverdue() { return !isReturned() && dueDate.isBefore(LocalDate.now()); }

    public long getDaysOverdue() {
        LocalDate end = returnDate != null ? returnDate : LocalDate.now();
        return Math.max(0, ChronoUnit.DAYS.between(dueDate, end));
    }

    public void completeReturn(LocalDate retDate, double fine) {
        this.returnDate = retDate;
        this.fine = fine;
    }

    @Override
    public String toString() {
        return String.format("%-10s | %-8s | %-16s | %-8s | %-10s | %-10s | %-8s | Rs.%.1f",
                txnId, studentId, (studentName != null && studentName.length() > 14 ? studentName.substring(0, 13) + "." : studentName),
                bookId, issueDate, dueDate, (isReturned() ? "RETURNED" : (isOverdue() ? "OVERDUE" : "ACTIVE")), fine);
    }
}
