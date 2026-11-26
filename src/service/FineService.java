package service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Overdue fine computation rules (Lectures 13-15).
 */
public class FineService {
    public static final double FINE_PER_DAY = 5.0;

    public static double calculate(LocalDate due, LocalDate ret) {
        if (due == null || ret == null) return 0.0;
        long days = Math.max(0, ChronoUnit.DAYS.between(due, ret));
        return days * FINE_PER_DAY;
    }

    public static void printReceipt(String stuId, String bookId, LocalDate due, LocalDate ret, double fine) {
        System.out.println("\n+-------------------------------------------------------------+");
        System.out.println("|                   NEXLIB OVERDUE FINE RECEIPT              |");
        System.out.println("+-------------------------------------------------------------+");
        System.out.printf("| Student ID : %-47s |%n", stuId);
        System.out.printf("| Book ID    : %-47s |%n", bookId);
        System.out.printf("| Due Date   : %-47s |%n", due);
        System.out.printf("| Return Date: %-47s |%n", ret);
        System.out.printf("| Overdue Day: %-47d |%n", Math.max(0, ChronoUnit.DAYS.between(due, ret)));
        System.out.printf("| Total Fine : Rs. %-43.2f |%n", fine);
        System.out.println("+-------------------------------------------------------------+");
    }
}
