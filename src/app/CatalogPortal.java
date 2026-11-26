package app;

import model.Book;
import service.LibraryService;
import util.ConsoleFormatter;
import util.InputHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for guest catalog browsing.
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

}
