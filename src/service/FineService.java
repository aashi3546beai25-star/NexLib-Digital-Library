package service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Service managing library penalty calculations, grace periods, and overdue policies.
 * Demonstrates Java Static members, Constants, and Method Overloading.
 */
public class FineService {

    // Static configuration constants
    public static final double FINE_PER_DAY = 5.0;
    public static final int STANDARD_BORROW_DAYS = 7;

    private FineService() { }

    /**
     * Standard fine calculation based on due date and actual return date.
     * Demonstrates Static method usage.
     */
    public static double calculate(LocalDate dueDate, LocalDate returnDate) {
        return calculate(dueDate, returnDate, FINE_PER_DAY);
    }

    /**
     * Overloaded calculation method allowing custom per-day rate.
     * Demonstrates OOP Method Overloading.
     */
    public static double calculate(LocalDate dueDate, LocalDate returnDate, double customRate) {
        if (dueDate == null || returnDate == null) return 0.0;
        long lateDays = ChronoUnit.DAYS.between(dueDate, returnDate);
        return lateDays > 0 ? lateDays * customRate : 0.0;
    }

    /**
     * Calculates the exact number of overdue days.
     */
    public static long calculateOverdueDays(LocalDate dueDate, LocalDate returnDate) {
        if (dueDate == null || returnDate == null) return 0;
        long days = ChronoUnit.DAYS.between(dueDate, returnDate);
        return days > 0 ? days : 0;
    }

    /**
     * Prints a breakdown invoice of the fine in terminal.
     */
    public static void printFineReceipt(String studentId, String bookId,
                                        LocalDate dueDate, LocalDate returnDate, double fine) {
        long lateDays = calculateOverdueDays(dueDate, returnDate);
        System.out.println("\n+-------------------------------------------------------------+");
        System.out.println("|                   NEXLIB FINE ASSESSMENT RECEIPT            |");
        System.out.println("+-------------------------------------------------------------+");
        System.out.printf("| Student ID      : %-42s |%n", studentId);
        System.out.printf("| Book Catalog ID : %-42s |%n", bookId);
        System.out.printf("| Prescribed Due  : %-42s |%n", dueDate);
        System.out.printf("| Actual Return   : %-42s |%n", returnDate);
        System.out.printf("| Overdue Days    : %-42s |%n", lateDays + " day(s)");
        System.out.printf("| Penalty Rate    : Rs. %-38.2f |%n", FINE_PER_DAY);
        System.out.printf("| Total Fine Due  : Rs. %-38.2f |%n", fine);
        System.out.println("+-------------------------------------------------------------+");
    }
}
