package model;

import java.io.Serializable;

/**
 * Book model implementing Comparable<Book> for natural sorting (Lectures 23-25).
 */
public class Book implements Serializable, Comparable<Book> {
    private static final long serialVersionUID = 1L;
    private String id, title, author, category, shelf;
    private boolean available = true;

    public Book(String id, String title, String author, String category, String shelf) {
        this.id = id != null ? id.trim().toUpperCase() : "";
        this.title = title != null ? title.trim() : "";
        this.author = author != null ? author.trim() : "";
        this.category = category != null ? category.trim() : "General";
        this.shelf = shelf != null ? shelf.trim() : "Rack-A1";
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public String getShelf() { return shelf; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean a) { this.available = a; }

    @Override
    public int compareTo(Book o) {
        return this.id.compareToIgnoreCase(o.id); // Natural order by Book ID
    }

    @Override
    public String toString() {
        return String.format("%-8s | %-26s | %-18s | %-15s | %-10s | %-9s",
                id, (title.length() > 24 ? title.substring(0, 23) + "." : title),
                (author.length() > 16 ? author.substring(0, 15) + "." : author),
                category, shelf, available ? "AVAILABLE" : "ISSUED");
    }
}
