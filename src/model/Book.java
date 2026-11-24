package model;

import java.io.Serializable;

/**
 * Represents a physical college library book equipped with an RFID transponder.
 * Demonstrates OOP Encapsulation, Constructor Overloading, and Data Integrity.
 */
public class Book implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private String title;
    private String author;
    private String category;
    private final String rfidTag;
    private boolean available;
    private String shelfLocation;

    /**
     * Comprehensive parameterized constructor.
     */
    public Book(String id, String title, String author, String category,
                String rfidTag, String shelfLocation) {
        this.id = id != null ? id.trim() : "";
        this.title = title != null ? title.trim() : "";
        this.author = author != null ? author.trim() : "";
        this.category = (category != null && !category.trim().isEmpty()) ? category.trim() : "General";
        this.rfidTag = rfidTag != null ? rfidTag.trim().toUpperCase() : "";
        this.available = true;
        this.shelfLocation = (shelfLocation != null && !shelfLocation.trim().isEmpty()) ? shelfLocation.trim() : "Rack-A1";
    }

    /**
     * Overloaded constructor with default shelf location.
     */
    public Book(String id, String title, String author, String category, String rfidTag) {
        this(id, title, author, category, rfidTag, "Rack-A1");
    }

    /**
     * Overloaded constructor maintaining backwards compatibility with existing 4-parameter calls.
     */
    public Book(String id, String title, String author, String rfidTag) {
        this(id, title, author, "Computer Science", rfidTag, "Rack-A1");
    }

    // Getters and Setters demonstrating OOP Encapsulation
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title.trim();
        }
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        if (author != null && !author.trim().isEmpty()) {
            this.author = author.trim();
        }
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        if (category != null && !category.trim().isEmpty()) {
            this.category = category.trim();
        }
    }

    public String getRfidTag() {
        return rfidTag;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getShelfLocation() {
        return shelfLocation;
    }

    public void setShelfLocation(String shelfLocation) {
        if (shelfLocation != null && !shelfLocation.trim().isEmpty()) {
            this.shelfLocation = shelfLocation.trim();
        }
    }

    @Override
    public String toString() {
        return String.format("%-8s | %-26s | %-18s | %-15s | %-12s | %-9s",
                id,
                title.length() > 26 ? title.substring(0, 23) + "..." : title,
                author.length() > 18 ? author.substring(0, 15) + "..." : author,
                category,
                rfidTag,
                available ? "AVAILABLE" : "ISSUED");
    }
}
