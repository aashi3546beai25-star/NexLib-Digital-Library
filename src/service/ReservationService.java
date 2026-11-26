package service;

import model.Reservation;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * FIFO reservation queue operations (Lectures 20-22).
 */
public class ReservationService {
    public static Queue<Reservation> getWaitingQueue(List<Reservation> all, String bookId) {
        Queue<Reservation> q = new LinkedList<>();
        for (Reservation r : all) {
            if (r.getBookId().equalsIgnoreCase(bookId) && "WAITING".equals(r.getStatus())) {
                q.offer(r);
            }
        }
        return q;
    }
}
