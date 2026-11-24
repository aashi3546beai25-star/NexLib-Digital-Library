package model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Tracks individual fine assessments, overdue penalties, and settlement statuses.
 * Demonstrates financial audit encapsulation and persistence.
 */
public class FineRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String fineId;
    private final String studentId;
    private final String bookId;
    private final double amount;
    private final LocalDate dateAssessed;
    private boolean paid;
    private LocalDate paymentDate;

    public FineRecord(String fineId, String studentId, String bookId,
                      double amount, LocalDate dateAssessed) {
        this.fineId = fineId;
        this.studentId = studentId;
        this.bookId = bookId;
        this.amount = amount;
        this.dateAssessed = dateAssessed;
        this.paid = false;
        this.paymentDate = null;
    }

    public String getFineId() {
        return fineId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getBookId() {
        return bookId;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDateAssessed() {
        return dateAssessed;
    }

    public boolean isPaid() {
        return paid;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void markAsPaid(LocalDate date) {
        this.paid = true;
        this.paymentDate = date != null ? date : LocalDate.now();
    }

    @Override
    public String toString() {
        return String.format("%-10s | %-10s | %-8s | Rs. %-7.2f | %-11s | %-8s | %-11s",
                fineId, studentId, bookId, amount, dateAssessed,
                (paid ? "PAID" : "UNPAID"),
                (paymentDate == null ? "-" : paymentDate.toString()));
    }
}
