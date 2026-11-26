package app;

import service.LibraryService;
import util.ConsoleFormatter;
import util.InputHelper;

/**
 * Open guest browsing portal for prospective students.
 */
public class CatalogPortal {
    private final LibraryService svc;

    public CatalogPortal(LibraryService s) { this.svc = s; }

    public void browse() {
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
