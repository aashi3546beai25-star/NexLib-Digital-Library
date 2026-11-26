package app;

import service.LibraryService;
import storage.DataStore;
import util.ConsoleFormatter;
import util.InputHelper;

/**
 * Main application coordinator for NexLib.
 * Routes users to Librarian, Student, or Open Catalog portals.
 */
public class LibraryApp {
    private final LibraryService library;
    private final DataStore store;
    private final LibrarianPortal librarianPortal;
    private final StudentPortal studentPortal;
    private final CatalogPortal catalogPortal;

    public LibraryApp(LibraryService library, DataStore store) {
        this.library = library;
        this.store = store;
        this.librarianPortal = new LibrarianPortal(library, store);
        this.studentPortal = new StudentPortal(library, store);
        this.catalogPortal = new CatalogPortal(library);
    }

    public void start() {
        boolean running = true;
        while (running) {
            ConsoleFormatter.printHeader("NEXLIB - DIGITAL COLLEGE LIBRARY & RFID BOOK LENDING SYSTEM");
            System.out.println("  1. Librarian Portal (Administrative Access)");
            System.out.println("  2. Student Portal (Self-Service Circulation)");
            System.out.println("  3. Browse Catalog & Search Books (Open Guest Access)");
            System.out.println("  4. Save & Exit");
            ConsoleFormatter.printDivider();

            int choice = InputHelper.readInt("Select portal option (1-4): ", 1, 4);
            switch (choice) {
                case 1 -> librarianPortal.login();
                case 2 -> studentPortal.login();
                case 3 -> catalogPortal.browse();
                case 4 -> {
                    store.save(library);
                    System.out.println("\n[SUCCESS] All library records safely saved to disk.");
                    System.out.println("Thank you for using NexLib. Goodbye!");
                    running = false;
                }
            }
        }
    }
}
