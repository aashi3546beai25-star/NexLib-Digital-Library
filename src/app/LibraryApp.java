package app;

import service.LibraryService;
import storage.DataStore;
import util.ConsoleFormatter;
import util.InputHelper;

/**
 * Main application coordinator routing users to Librarian, Student, or Catalog browsing.
 */
public class LibraryApp {
    private final LibraryService svc;
    private final DataStore store;

    public LibraryApp(LibraryService svc, DataStore store) {
        this.svc = svc;
        this.store = store;
    }

    public void start() {
        boolean running = true;
        while (running) {
            ConsoleFormatter.printHeader("NEXLIB - DIGITAL COLLEGE LIBRARY MANAGEMENT SYSTEM");
            System.out.println("  1. Librarian Portal");
            System.out.println("  2. Student Portal");
            System.out.println("  3. Browse Catalog & Search Books");
            System.out.println("  4. Save & Exit");
            ConsoleFormatter.printDivider();

            int choice = InputHelper.readInt("Select portal option: ", 1, 4);
            switch (choice) {
                case 1 -> new LibrarianPortal(svc, store).login();
                case 2 -> new StudentPortal(svc, store).login();
                case 3 -> browseCatalog();
                case 4 -> {
                    if (svc.getAuditDaemon() != null) svc.getAuditDaemon().stop();
                    store.save(svc);
                    System.out.println("\nSUCCESS: All records safely saved. Goodbye!");
                    running = false;
                }
            }
        }
    }

    private void browseCatalog() {
        boolean active = true;
        while (active) {
            ConsoleFormatter.printHeader("OPEN CATALOG BROWSER");
            System.out.println(" 1. View All Books");
            System.out.println(" 2. Search Books");
            System.out.println(" 3. View Available Books Only");
            System.out.println(" 4. View Unique Categories");
            System.out.println(" 5. Back to Main Menu");
            ConsoleFormatter.printDivider();

            int ch = InputHelper.readInt("Choice: ", 1, 5);
            switch (ch) {
                case 1 -> ConsoleFormatter.printBookTable(svc.getBooks());
                case 2 -> {
                    String q = InputHelper.readLine("Keyword: ");
                    ConsoleFormatter.printBookTable(svc.searchBooks(q, ""));
                }
                case 3 -> ConsoleFormatter.printBookTable(svc.getAvailableBooksStream());
                case 4 -> {
                    System.out.println("\nCategories:");
                    svc.getUniqueCategoriesSorted().forEach(c -> System.out.println(" • " + c));
                }
                case 5 -> active = false;
            }
            InputHelper.pressEnterToContinue();
        }
    }
}
