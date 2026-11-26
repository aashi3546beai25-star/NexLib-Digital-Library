package model;

/**
 * Student model inheriting from User. Demonstrates OOP Inheritance.
 */
public class Student extends User {
    private static final long serialVersionUID = 1L;
    private String department;
    private int maxBooks = 3;

    public Student(String id, String name, String dept) {
        super(id, name, "pass123");
        this.department = dept;
    }

    public Student(String id, String name, String dept, String pass) {
        super(id, name, pass);
        this.department = dept;
    }

    public String getDepartment() { return department; }
    public int getMaxBooks() { return maxBooks; }

    @Override public String getRole() { return "STUDENT"; }
    @Override public void displayProfile() {
        System.out.printf("STUDENT PROFILE: ID: %s | Name: %s | Dept: %s | Limit: %d books%n", id, name, department, maxBooks);
    }
}
