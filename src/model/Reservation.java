package model;

import java.io.Serializable;

/**
 * Reservation waiting queue model (Lectures 20-22).
 */
public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
    private String resId, studentId, studentName, bookId, bookTitle, status = "WAITING";

    public Reservation(String resId, String stuId, String stuName, String bookId, String title) {
        this.resId = resId; this.studentId = stuId; this.studentName = stuName;
        this.bookId = bookId; this.bookTitle = title;
    }

    public String getResId() { return resId; }
    public String getStudentId() { return studentId; }
    public String getBookId() { return bookId; }
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }

    @Override
    public String toString() {
        return String.format("%-8s | %-8s | %-16s | %-8s | %-10s", resId, studentId, studentName, bookId, status);
    }
}
