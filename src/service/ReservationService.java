package service;

import model.Reservation;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Manages book reservations using a FIFO (First-In, First-Out) waiting queue.
 * Demonstrates the use of Java Queue and LinkedList collections.
 */
public class ReservationService {

    private ReservationService() { }

    /**
     * Finds and activates the next student in the waiting queue when a book is returned.
     * Uses FIFO order: the earliest reservation gets activated first.
     */
    public static Reservation activateNext(List<Reservation> reservations, String bookId) {
        if (reservations == null || bookId == null) return null;

        for (Reservation reservation : reservations) {
            if (reservation.getBookId().equalsIgnoreCase(bookId)
                    && "WAITING".equalsIgnoreCase(reservation.getStatus())) {
                reservation.setStatus("AVAILABLE");
                return reservation;
            }
        }
        return null;
    }

    /**
     * Constructs a FIFO Queue of pending reservations for a specific book.
     * Demonstrates Queue interface and LinkedList implementation.
     */
    public static Queue<Reservation> getWaitingQueue(List<Reservation> reservations, String bookId) {
        Queue<Reservation> queue = new LinkedList<>();
        if (reservations == null || bookId == null) return queue;

        for (Reservation r : reservations) {
            if (r.getBookId().equalsIgnoreCase(bookId) && "WAITING".equalsIgnoreCase(r.getStatus())) {
                queue.offer(r); // FIFO queue insertion
            }
        }
        return queue;
    }

    /**
     * Determines a student's numerical position in the waiting line.
     */
    public static int getPositionInQueue(List<Reservation> reservations, Reservation target) {
        if (reservations == null || target == null) return -1;
        if ("AVAILABLE".equalsIgnoreCase(target.getStatus())) return 0; // ready for pickup

        int position = 0;
        for (Reservation r : reservations) {
            if (r.getBookId().equalsIgnoreCase(target.getBookId()) && "WAITING".equalsIgnoreCase(r.getStatus())) {
                position++;
                if (r.getReservationId().equals(target.getReservationId())
                        || (r.getStudentId().equals(target.getStudentId()) && r.getBookId().equals(target.getBookId()))) {
                    return position;
                }
            }
        }
        return -1;
    }

    /**
     * Verifies if a student has an existing active or waiting reservation for a given book.
     */
    public static boolean hasActiveReservation(List<Reservation> reservations, String studentId, String bookId) {
        if (reservations == null) return false;
        for (Reservation r : reservations) {
            if (r.getStudentId().equalsIgnoreCase(studentId)
                    && r.getBookId().equalsIgnoreCase(bookId)
                    && ("WAITING".equalsIgnoreCase(r.getStatus()) || "AVAILABLE".equalsIgnoreCase(r.getStatus()))) {
                return true;
            }
        }
        return false;
    }
}
