package model;

/**
 * Represents a registered college student who can borrow, return, and reserve books.
 * Inherits from abstract User class, demonstrating OOP Inheritance and Polymorphism.
 */
public class Student extends User {
    private static final long serialVersionUID = 1L;

    private String email;
    private String department;
    private String contactNumber;
    private int maxBooksAllowed;

    /**
     * Primary constructor demonstrating constructor chaining to superclass.
     */
    public Student(String id, String name, String email, String password,
                   String department, String contactNumber, int maxBooksAllowed) {
        super(id, name, password);
        this.email = email;
        this.department = (department == null || department.trim().isEmpty()) ? "General" : department;
        this.contactNumber = (contactNumber == null || contactNumber.trim().isEmpty()) ? "N/A" : contactNumber;
        this.maxBooksAllowed = maxBooksAllowed > 0 ? maxBooksAllowed : 3;
    }

    /**
     * Overloaded constructor with default borrowing limit.
     */
    public Student(String id, String name, String email, String password,
                   String department, String contactNumber) {
        this(id, name, email, password, department, contactNumber, 3);
    }

    /**
     * Backwards-compatible constructor for existing project signatures.
     */
    public Student(String id, String name, String email) {
        this(id, name, email, "student123", "General", "N/A", 3);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null && !email.trim().isEmpty()) {
            this.email = email.trim();
        }
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        if (department != null && !department.trim().isEmpty()) {
            this.department = department.trim();
        }
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        if (contactNumber != null && !contactNumber.trim().isEmpty()) {
            this.contactNumber = contactNumber.trim();
        }
    }

    public int getMaxBooksAllowed() {
        return maxBooksAllowed;
    }

    public void setMaxBooksAllowed(int maxBooksAllowed) {
        if (maxBooksAllowed > 0) {
            this.maxBooksAllowed = maxBooksAllowed;
        }
    }

    @Override
    public String getRole() {
        return "Student";
    }

    @Override
    public void displayProfile() {
        System.out.println("----------------------------------------");
        System.out.println("         STUDENT PROFILE SUMMARY        ");
        System.out.println("----------------------------------------");
        System.out.println("Student ID     : " + getId());
        System.out.println("Full Name      : " + getName());
        System.out.println("Email Address  : " + email);
        System.out.println("Department     : " + department);
        System.out.println("Contact Number : " + contactNumber);
        System.out.println("Borrow Limit   : " + maxBooksAllowed + " books at a time");
        System.out.println("Role           : " + getRole());
        System.out.println("----------------------------------------");
    }

    @Override
    public String toString() {
        return String.format("%-10s | %-20s | %-22s | %-15s",
                getId(), getName(), email, department);
    }
}
