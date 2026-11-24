package model;

import java.io.Serializable;

/**
 * Abstract base class representing a user of the NexLib system.
 * Demonstrates OOP Abstraction, Encapsulation, and Inheritance.
 */
public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;

    // Encapsulated fields with private/protected access
    private final String id;
    private String name;
    private String password;

    /**
     * Parameterized constructor demonstrating constructor initialization.
     */
    public User(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = (password == null || password.trim().isEmpty()) ? "password123" : password;
    }

    /**
     * Constructor overload for backwards compatibility.
     */
    public User(String id, String name) {
        this(id, name, "password123");
    }

    // Getters and Setters demonstrating Encapsulation
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
    }

    public String getPassword() {
        return (password != null && !password.trim().isEmpty()) ? password : "pass123";
    }

    public boolean verifyPassword(String candidatePassword) {
        if (candidatePassword == null) return false;
        String current = (this.password != null && !this.password.trim().isEmpty()) ? this.password : "pass123";
        return current.equalsIgnoreCase(candidatePassword.trim());
    }

    public void setPassword(String newPassword) {
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            this.password = newPassword.trim();
        }
    }

    /**
     * Abstract method to be implemented by child classes (Student, Librarian).
     * Demonstrates OOP Abstraction and Polymorphism.
     */
    public abstract String getRole();

    /**
     * Abstract method to format and display user profile details.
     */
    public abstract void displayProfile();
}
