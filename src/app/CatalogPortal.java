package app;

import model.Book;
import service.LibraryService;
import util.ConsoleFormatter;
import util.InputHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for guest catalog browsing and technical viva demonstration.
 */
public class CatalogPortal {
    private final LibraryService library;

    public CatalogPortal(LibraryService library) {
        this.library = library;
    }

    public void browse() {
        boolean back = false;
        while (!back) {
            ConsoleFormatter.printHeader("OPEN CATALOG BROWSER (GUEST ACCESS)");
            System.out.println(" 1. View All Books In Catalog");
            System.out.println(" 2. Search Books");
            System.out.println(" 3. View Available Books Only");
            System.out.println(" 4. Back to Main Menu");
            ConsoleFormatter.printDivider();

            int choice = InputHelper.readInt("Choice (1-4): ", 1, 4);
            switch (choice) {
                case 1 -> { ConsoleFormatter.printBookTable(library.getBooks()); InputHelper.pressEnterToContinue(); }
                case 2 -> searchBooks();
                case 3 -> displayAvailableBooks();
                case 4 -> back = true;
            }
        }
    }

    private void searchBooks() {
        ConsoleFormatter.printSubHeader("SEARCH BOOK CATALOG");
        System.out.println("1. General Search (Title, Author, Category, ID) | 2. Filter by Category");
        int mode = InputHelper.readInt("Select mode (1-2): ", 1, 2);

        List<Book> results;
        if (mode == 1) {
            String q = InputHelper.readLine("Enter search keyword: ");
            results = library.searchBooks(q);
        } else {
            String cat = InputHelper.readLine("Enter Category: ");
            String q = InputHelper.readLine("Enter Keyword (optional): ", true);
            results = library.searchBooks(q, cat);
        }
        ConsoleFormatter.printBookTable(results);
        InputHelper.pressEnterToContinue();
    }

    private void displayAvailableBooks() {
        List<Book> available = new ArrayList<>();
        for (Book b : library.getBooks()) {
            if (b.isAvailable()) available.add(b);
        }
        ConsoleFormatter.printBookTable(available);
        System.out.println("Available for borrowing: " + available.size() + " book(s).");
        InputHelper.pressEnterToContinue();
    }

    public void displayVivaGuide() {
        ConsoleFormatter.printHeader("NEXLIB TECHNICAL ARCHITECTURE & VIVA GUIDE");
        System.out.println("1. OOP Concepts: Classes/Objects, Encapsulation, Inheritance (User -> Student/Librarian),");
        System.out.println("   Abstraction (User & LibraryService), Polymorphism (displayProfile, searchBooks overloads).");
        System.out.println("2. Collections: ArrayList (lists), HashMap (O(1) RFID lookup), Queue/LinkedList (FIFO reservations).");
        System.out.println("3. Custom Exceptions: BookNotFoundException, BookUnavailableException, DuplicateBookException, etc.");
        System.out.println("4. Persistence: Java Object Serialization (DataStore) with atomic .bak backup.");
        System.out.println("5. Hardware Simulation: RFIDScanner simulates 13.56 MHz RFID readers via console.");
        ConsoleFormatter.printDivider();
        InputHelper.pressEnterToContinue();
    }
}
