package model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents a reservation queue entry for a book that is currently checked out.
 * Integrates with Java Queue/LinkedList data structure for FIFO prioritization.
 */
public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String reservationId;
    private final String studentId;
    private final String bookId;
    private String bookTitle;
    private final LocalDate reservationDate;
    private String status; // WAITING, AVAILABLE, FULFILLED, CANCELLED

    /**
     * Primary constructor with explicit reservation ID.
     */
    public Reservation(String reservationId, String studentId, String bookId, String bookTitle) {
        this.reservationId = reservationId;
        this.studentId = studentId;
        this.bookId = bookId;
        this.bookTitle = bookTitle != null ? bookTitle : "N/A";
        this.reservationDate = LocalDate.now();
        this.status = "WAITING";
    }

    /**
     * Backwards-compatible constructor for existing project signatures.
     */
    public Reservation(String studentId, String bookId) {
        this("RES-" + System.currentTimeMillis() % 100000, studentId, bookId, "N/A");
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getStudentId() {
        return studentId;
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

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status != null && !status.trim().isEmpty()) {
            this.status = status.trim().toUpperCase();
        }
    }

    public void fulfill() {
        this.status = "FULFILLED";
    }

    public void cancel() {
        this.status = "CANCELLED";
    }

    @Override
    public String toString() {
        return String.format("%-10s | %-10s | %-8s | %-12s | %-10s",
                reservationId, studentId, bookId, reservationDate, status);
    }
}
