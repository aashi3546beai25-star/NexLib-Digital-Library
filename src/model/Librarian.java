package model;

/**
 * Librarian staff model inheriting from User.
 */
public class Librarian extends User {
    private static final long serialVersionUID = 1L;
    private String designation;

    public Librarian(String id, String name, String pass, String desig) {
        super(id, name, pass);
        this.designation = desig;
    }

    public String getDesignation() { return designation; }
    @Override public String getRole() { return "LIBRARIAN"; }
    @Override public void displayProfile() {
        System.out.printf("LIBRARIAN PROFILE: ID: %s | Name: %s | Role: %s%n", id, name, designation);
    }
}
