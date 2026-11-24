package model;

/**
 * Represents a library staff member/administrator with privileges to manage books,
 * circulation, students, and institutional reporting.
 * Demonstrates OOP Inheritance and Polymorphic behavior.
 */
public class Librarian extends User {
    private static final long serialVersionUID = 1L;

    private String employeeCode;
    private String designation;

    /**
     * Primary constructor demonstrating constructor chaining to superclass.
     */
    public Librarian(String id, String name, String password,
                     String employeeCode, String designation) {
        super(id, name, password);
        this.employeeCode = (employeeCode == null || employeeCode.trim().isEmpty()) ? "EMP-LIB-01" : employeeCode;
        this.designation = (designation == null || designation.trim().isEmpty()) ? "Librarian" : designation;
    }

    /**
     * Backwards-compatible constructor for existing project signatures.
     */
    public Librarian(String id, String name) {
        this(id, name, "admin123", "EMP-LIB-01", "Head Librarian");
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        if (employeeCode != null && !employeeCode.trim().isEmpty()) {
            this.employeeCode = employeeCode.trim();
        }
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        if (designation != null && !designation.trim().isEmpty()) {
            this.designation = designation.trim();
        }
    }

    @Override
    public String getRole() {
        return "Librarian";
    }

    @Override
    public void displayProfile() {
        System.out.println("----------------------------------------");
        System.out.println("        LIBRARIAN STAFF PROFILE         ");
        System.out.println("----------------------------------------");
        System.out.println("Staff ID       : " + getId());
        System.out.println("Employee Code  : " + employeeCode);
        System.out.println("Full Name      : " + getName());
        System.out.println("Designation    : " + designation);
        System.out.println("Access Level   : Full Administrative Privileges");
        System.out.println("Role           : " + getRole());
        System.out.println("----------------------------------------");
    }

    @Override
    public String toString() {
        return String.format("%-10s | %-20s | %-12s | %-15s",
                getId(), getName(), employeeCode, designation);
    }
}
