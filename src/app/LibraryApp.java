package app;

import service.LibraryService;
import storage.DataStore;
import util.ConsoleFormatter;
import util.InputHelper;

/**
 * Top-level menu coordinator routing users to Librarian, Student, or Guest portals.
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
                case 3 -> new CatalogPortal(svc).browse();
                case 4 -> {
                    if (svc.getAuditDaemon() != null) svc.getAuditDaemon().stop();
                    store.save(svc);
                    System.out.println("\nSUCCESS: All records safely saved. Goodbye!");
                    running = false;
                }
            }
        }
    }
}
