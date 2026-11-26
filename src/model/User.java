package model;

import java.io.Serializable;

/**
 * Abstract user entity demonstrating OOP Abstraction and Encapsulation.
 */
public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String id, name, password;

    public User(String id, String name, String password) {
        this.id = id != null ? id.trim() : "";
        this.name = name != null ? name.trim() : "";
        this.password = password != null ? password.trim() : "";
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public boolean verifyPassword(String pass) { return this.password.equals(pass); }
    public abstract String getRole();
    public abstract void displayProfile();
}
