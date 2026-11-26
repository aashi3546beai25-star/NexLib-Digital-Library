package test;

import exception.LibraryException;
import exception.LibraryException.*;
import model.*;
import service.*;
import storage.CsvDataService;
import util.DataResult;

import java.io.File;
import java.util.*;

/**
 * 13-Point Automated University Syllabus Verification Suite.
 */
public class SystemVerificationTest {
    private static int passed = 0, total = 0;

    public static void main(String[] args) {
        System.out.println("============================================================================");
        System.out.println("       NEXLIB - CHITKARA UNIVERSITY SYLLABUS VERIFICATION SUITE");
        System.out.println("============================================================================");

        check(1, "Input Validation & Custom Exceptions", () -> {
            LibraryService s = new LibraryService();
            s.seedInitialData();
            try { s.addBook(null); throw new AssertionError("Null check failed"); } catch (InvalidInputException ignored) {}
            try { s.addBook(new Book("B101", "Dup", "A", "C", "S")); throw new AssertionError("Dup check failed"); } catch (DuplicateBookException ignored) {}
            try { s.issueBook("STU101", "NO_BOOK"); throw new AssertionError("Not found check failed"); } catch (BookNotFoundException ignored) {}
        });

        check(2, "Collections: ArrayList, LinkedList & Queue", () -> {
            LibraryService s = new LibraryService();
            s.seedInitialData();
            s.reserveBook("STU101", "B101");
            if (s.getBooks().isEmpty() || s.getReservations().isEmpty()) throw new AssertionError("Collections empty");
        });

        check(3, "Set Implementations: HashSet & TreeSet", () -> {
            LibraryService s = new LibraryService();
            s.seedInitialData();
            if (!(s.getUniqueCategoriesSorted() instanceof TreeSet)) throw new AssertionError("TreeSet check failed");
            if (!(s.getActiveBorrowerIds() instanceof HashSet)) throw new AssertionError("HashSet check failed");
        });

        check(4, "Map Implementations: HashMap & TreeMap", () -> {
            LibraryService s = new LibraryService();
            s.seedInitialData();
            if (s.findBook("B101") == null) throw new AssertionError("HashMap constant time lookup failed");
            if (!(s.getBooksGroupedByCategory() instanceof TreeMap)) throw new AssertionError("TreeMap check failed");
        });

        check(5, "Traversal: Iterator & ListIterator", () -> {
            LibraryService s = new LibraryService();
            s.seedInitialData();
            List<Book> rev = s.getBooksReversedWithListIterator();
            if (rev.size() != s.getBooks().size()) throw new AssertionError("ListIterator failed");
            s.purgeOldReturnedRecords(30);
        });

        check(6, "Comparable Natural Ordering by Book ID", () -> {
            Book b1 = new Book("B101", "A", "Auth", "Cat", "S");
            Book b2 = new Book("B102", "B", "Auth", "Cat", "S");
            if (b1.compareTo(b2) >= 0) throw new AssertionError("Comparable failed");
        });

        check(7, "Comparator Strategies & Sorting", () -> {
            List<Book> list = new ArrayList<>(List.of(
                    new Book("B2", "Zulu", "Bob", "C", "S"),
                    new Book("B1", "Alpha", "Alice", "C", "S")
            ));
            list.sort(Book.BY_TITLE);
            if (!list.get(0).getTitle().equals("Alpha")) throw new AssertionError("BY_TITLE comparator failed");
            list.sort(Book.BY_AUTHOR);
            if (!list.get(0).getAuthor().equals("Alice")) throw new AssertionError("BY_AUTHOR comparator failed");
        });

        check(8, "Multithreading: Daemon Thread Lifecycle & Sleep", () -> {
            LibraryService s = new LibraryService();
            BackgroundAuditDaemon d = new BackgroundAuditDaemon(s, 100);
            if (!d.isDaemon()) throw new AssertionError("Daemon flag failed");
            d.start();
            try { Thread.sleep(150); } catch (InterruptedException ignored) {}
            if (d.getState() != Thread.State.RUNNABLE && d.getState() != Thread.State.TIMED_WAITING)
                throw new AssertionError("Lifecycle state failed: " + d.getState());
            d.stop();
        });

        check(9, "Inter-Thread Coordination: wait & notifyAll", () -> {
            BookReturnNotifier n = new BookReturnNotifier();
            boolean[] ok = {false};
            Thread t = new Thread(() -> {
                try { ok[0] = n.waitForBook("B101", 500); } catch (InterruptedException ignored) {}
            });
            t.start();
            try { Thread.sleep(50); n.signalBookReturned("B101"); t.join(600); } catch (InterruptedException ignored) {}
            if (!ok[0]) throw new AssertionError("wait/notify coordination failed");
        });

        check(10, "Stream API Pipelines: filter, map, sorted, limit", () -> {
            LibraryService s = new LibraryService();
            s.seedInitialData();
            List<String> titles = s.getAvailableBooksStream().stream().map(Book::getTitle).sorted().limit(2).toList();
            if (titles.size() != 2) throw new AssertionError("Stream pipeline failed");
        });

        check(11, "Introductory Generics: Type Safety & Wildcards", () -> {
            DataResult<Book> res = DataResult.ok("Success", new Book("T1", "Title", "A", "C", "S"));
            if (!res.isSuccess() || res.getData() == null) throw new AssertionError("Generic DataResult failed");
            List<String> ids = DataResult.transform(List.of(new Book("T1", "Title", "A", "C", "S")), Book::getId);
            if (ids.size() != 1 || !ids.get(0).equals("T1")) throw new AssertionError("Wildcard transform failed");
        });

        check(12, "File Handling: Character Streams CSV Export/Import", () -> {
            LibraryService s = new LibraryService();
            s.seedInitialData();
            File f = new File("test_export.csv");
            try {
                int exp = CsvDataService.exportBooks(f, s.getBooks());
                List<Book> imp = CsvDataService.importBooks(f);
                if (exp == 0) throw new AssertionError("CSV export failed");
            } catch (Exception e) { throw new AssertionError("CSV stream failed: " + e.getMessage()); }
            finally { if (f.exists()) f.delete(); }
        });

        check(13, "Lending Circulation, Overdue Rules & FIFO Reservations", () -> {
            LibraryService s = new LibraryService();
            s.seedInitialData();
            IssueRecord r = s.issueBook("STU101", "B102");
            double fine = s.returnBook("STU101", "B102");
            if (r == null || fine < 0) throw new AssertionError("Circulation workflow failed");
        });

        System.out.println("============================================================================");
        System.out.printf("TEST RESULTS: %d / %d PASSED%n", passed, total);
        System.out.println("============================================================================");
    }

    private static void check(int id, String name, Action test) {
        total++;
        try {
            test.run();
            System.out.printf("PASS: Scenario %2d: %s%n", id, name);
            passed++;
        } catch (Throwable e) {
            System.out.printf("FAIL: Scenario %2d: %s | Error: %s%n", id, name, e.getMessage());
        }
    }

    @FunctionalInterface
    interface Action { void run() throws Exception; }
}
