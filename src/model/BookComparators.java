package model;

import java.util.Comparator;

/**
 * Custom Comparator implementations (Lectures 23-25).
 */
public class BookComparators {
    private BookComparators() {}

    public static final Comparator<Book> BY_TITLE = Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER);
    public static final Comparator<Book> BY_AUTHOR = Comparator.comparing(Book::getAuthor, String.CASE_INSENSITIVE_ORDER);
    public static final Comparator<Book> BY_CATEGORY = Comparator.comparing(Book::getCategory, String.CASE_INSENSITIVE_ORDER).thenComparing(Book::getTitle);
}
