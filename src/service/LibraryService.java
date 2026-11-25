package service;

import exception.LibraryException;
import exception.LibraryException.*;
import model.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

/**
 * Core business service managing books, students, loans, reservations, and reports.
 * Implements Serializable to persist all data.
 */
public class LibraryService implements Serializable {
    private static final long serialVersionUID = 1L;

    // ArrayLists for storing library records
    private List<Book> books = new ArrayList<>();
    private List<Student> students = new ArrayList<>();
    private List<Librarian> librarians = new ArrayList<>();
    private List<IssueRecord> issueRecords = new ArrayList<>();
    private List<Reservation> reservations = new ArrayList<>();

    // HashMap for fast O(1) RFID tag to Book lookup
    private transient Map<String, Book> rfidToBookMap = new HashMap<>();

    public LibraryService() {
        rebuildIndices();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        rebuildIndices();
    }

    /**
     * Rebuilds HashMap index and initializes default admin if needed.
     */
    public synchronized void rebuildIndices() {
        if (books == null) books = new ArrayList<>();
        if (students == null) students = new ArrayList<>();
        if (librarians == null) librarians = new ArrayList<>();
        if (issueRecords == null) issueRecords = new ArrayList<>();
        if (reservations == null) reservations = new ArrayList<>();

        if (rfidToBookMap == null) {
            rfidToBookMap = new HashMap<>();
        }
        rfidToBookMap.clear();

        for (Book b : books) {
            if (b.getRfidTag() != null && !b.getRfidTag().isEmpty()) {
                rfidToBookMap.put(b.getRfidTag().toUpperCase(), b);
            }
        }

        if (librarians.isEmpty()) {
            librarians.add(new Librarian("admin", "Chief Librarian", "admin123", "LIB-001", "Head Librarian"));
        }

        for (Student s : students) {
            if (s.getPassword() == null || s.getPassword().trim().isEmpty()) {
                s.setPassword("pass123");
            }
        }

        for (IssueRecord r : issueRecords) {
            if (r.getStudentName() == null || r.getStudentName().equals("N/A")) {
                Student s = findStudent(r.getStudentId());
                if (s != null) r.setStudentName(s.getName());
            }
        }

        for (Reservation res : reservations) {
            if (res.getStudentName() == null || res.getStudentName().equals("N/A")) {
                Student s = findStudent(res.getStudentId());
                if (s != null) res.setStudentName(s.getName());
            }
        }
    }

    /**
     * Seeds initial sample college books and students if starting fresh.
     */
    public void seedInitialDataIfEmpty() {
        if (books.isEmpty()) {
            try {
                addBook(new Book("B101", "Data Structures & Algorithms in Java", "Robert Lafore", "Computer Science", "RFID-CS-101", "Rack-CS-1"));
                addBook(new Book("B102", "Operating System Concepts", "Silberschatz & Galvin", "Computer Science", "RFID-CS-102", "Rack-CS-1"));
                addBook(new Book("B103", "Digital Logic & Microprocessors", "M. Morris Mano", "Electronics", "RFID-EC-201", "Rack-EC-2"));
                addBook(new Book("B104", "Higher Engineering Mathematics", "Dr. B. S. Grewal", "Mathematics", "RFID-MA-301", "Rack-MA-3"));
                addBook(new Book("B105", "Fluid Mechanics & Thermodynamics", "Frank M. White", "Mechanical", "RFID-ME-401", "Rack-ME-4"));
                addBook(new Book("B106", "Artificial Intelligence: A Modern Approach", "Russell & Norvig", "Computer Science", "RFID-CS-106", "Rack-CS-2"));
            } catch (LibraryException ignored) { }
        }

        if (students.isEmpty()) {
            try {
                registerStudent(new Student("STU101", "Aarav Sharma", "aarav@college.edu", "pass123", "Computer Science", "+91-9876543210", 3));
                registerStudent(new Student("STU102", "Diya Patel", "diya@college.edu", "pass123", "Electronics & Comm", "+91-9876543211", 3));
                registerStudent(new Student("STU103", "Rohan Verma", "rohan@college.edu", "pass123", "Mechanical Eng", "+91-9876543212", 3));
            } catch (LibraryException ignored) { }
        }
    }

    // =========================================================================
    // BOOK OPERATIONS
    // =========================================================================

    public synchronized void addBook(Book book) throws LibraryException {
        if (book == null) throw new InvalidInputException("Book cannot be null.");
        if (book.getId() == null || book.getId().trim().isEmpty()) {
            throw new InvalidInputException("Book ID cannot be empty.");
        }
        if (book.getRfidTag() == null || book.getRfidTag().trim().isEmpty()) {
            throw new InvalidRFIDException("Book RFID tag cannot be empty.");
        }
        if (findBookById(book.getId()) != null) {
            throw new DuplicateBookException("Book ID '" + book.getId() + "' already exists.");
        }

        String tag = book.getRfidTag().toUpperCase();
        if (rfidToBookMap.containsKey(tag)) {
            throw new DuplicateBookException("RFID tag '" + tag + "' is already assigned.");
        }

        books.add(book);
        rfidToBookMap.put(tag, book);
    }

    public synchronized void updateBook(String id, String title, String author, String cat, String shelf) throws LibraryException {
        Book b = findBookById(id);
        if (b == null) throw new BookNotFoundException("No book found with ID: " + id);
        if (title != null && !title.trim().isEmpty()) b.setTitle(title);
        if (author != null && !author.trim().isEmpty()) b.setAuthor(author);
        if (cat != null && !cat.trim().isEmpty()) b.setCategory(cat);
        if (shelf != null && !shelf.trim().isEmpty()) b.setShelfLocation(shelf);
    }

    public synchronized void deleteBook(String id) throws LibraryException {
        Book b = findBookById(id);
        if (b == null) throw new BookNotFoundException("No book found with ID: " + id);
        if (!b.isAvailable()) {
            throw new BookUnavailableException("Cannot delete book '" + b.getTitle() + "' because it is currently issued.");
        }
        books.remove(b);
        rfidToBookMap.remove(b.getRfidTag().toUpperCase());
    }

    public Book findBookById(String id) {
        if (id == null) return null;
        for (Book b : books) {
            if (b.getId().equalsIgnoreCase(id.trim())) return b;
        }
        return null;
    }

    public Book findBookByRfid(String rfid) {
        if (rfid == null) return null;
        return rfidToBookMap.get(rfid.trim().toUpperCase());
    }

    public List<Book> getBooks() {
        return books;
    }

    public List<Book> searchBooks(String query) {
        List<Book> list = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) return list;
        String q = query.toLowerCase().trim();
        for (Book b : books) {
            if (b.getId().toLowerCase().contains(q)
                    || b.getTitle().toLowerCase().contains(q)
                    || b.getAuthor().toLowerCase().contains(q)
                    || b.getCategory().toLowerCase().contains(q)
                    || b.getRfidTag().toLowerCase().contains(q)) {
                list.add(b);
            }
        }
        return list;
    }

    public List<Book> searchBooks(String query, String category) {
        List<Book> list = new ArrayList<>();
        String q = (query != null) ? query.toLowerCase().trim() : "";
        String cat = (category != null) ? category.toLowerCase().trim() : "";

        for (Book b : books) {
            boolean matchCat = cat.isEmpty() || b.getCategory().toLowerCase().contains(cat);
            boolean matchQ = q.isEmpty()
                    || b.getTitle().toLowerCase().contains(q)
                    || b.getAuthor().toLowerCase().contains(q)
                    || b.getId().toLowerCase().contains(q);

            if (matchCat && matchQ) list.add(b);
        }
        return list;
    }

    // =========================================================================
    // STUDENT OPERATIONS
    // =========================================================================

    public synchronized void registerStudent(Student student) throws LibraryException {
        if (student == null || student.getId() == null || student.getId().trim().isEmpty()) {
            throw new InvalidInputException("Student details are invalid.");
        }
        if (findStudent(student.getId()) != null) {
            throw new LibraryException("Student ID '" + student.getId() + "' already exists.");
        }
        students.add(student);
    }

    public Student findStudent(String id) {
        if (id == null) return null;
        for (Student s : students) {
            if (s.getId().equalsIgnoreCase(id.trim())) return s;
        }
        return null;
    }

    public List<Student> getStudents() {
        return students;
    }

    // =========================================================================
    // CIRCULATION & RFID LENDING
    // =========================================================================

    public synchronized IssueRecord issueBook(String studentId, String rfid) throws LibraryException {
        Student student = findStudent(studentId);
        if (student == null) {
            throw new StudentNotFoundException("Student ID '" + studentId + "' is not registered.");
        }
        if (rfid == null || rfid.trim().isEmpty()) {
            throw new InvalidRFIDException("RFID tag cannot be empty.");
        }

        Book book = findBookByRfid(rfid);
        if (book == null) {
            throw new BookNotFoundException("No book matches RFID tag: [" + rfid + "].");
        }
        if (!book.isAvailable()) {
            throw new BookUnavailableException("Book '" + book.getTitle() + "' (ID: " + book.getId() + ") is currently checked out.");
        }

        int currentBorrowCount = 0;
        for (IssueRecord r : issueRecords) {
            if (r.getStudentId().equalsIgnoreCase(studentId) && !r.isReturned()) {
                currentBorrowCount++;
            }
        }
        if (currentBorrowCount >= student.getMaxBooksAllowed()) {
            throw new LibraryException("Student reached maximum limit of " + student.getMaxBooksAllowed() + " books.");
        }

        Queue<Reservation> waitingQueue = ReservationService.getWaitingQueue(reservations, book.getId());
        if (!waitingQueue.isEmpty()) {
            Reservation top = waitingQueue.peek();
            if (!top.getStudentId().equalsIgnoreCase(studentId)) {
                throw new BookUnavailableException("This book is reserved for student ID '" + top.getStudentId() + "'.");
            } else {
                top.fulfill();
            }
        }

        for (Reservation r : reservations) {
            if (r.getBookId().equalsIgnoreCase(book.getId())
                    && r.getStudentId().equalsIgnoreCase(studentId)
                    && "AVAILABLE".equalsIgnoreCase(r.getStatus())) {
                r.fulfill();
                break;
            }
        }

        book.setAvailable(false);
        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(FineService.STANDARD_BORROW_DAYS);
        String txnId = "TXN-" + (issueRecords.size() + 101);

        IssueRecord record = new IssueRecord(txnId, studentId, student.getName(), book.getId(), book.getTitle(), issueDate, dueDate);
        issueRecords.add(record);
        return record;
    }

    public synchronized double returnBook(String studentId, String rfid) throws LibraryException {
        Student student = findStudent(studentId);
        if (student == null) throw new StudentNotFoundException("Student ID not found.");

        Book book = findBookByRfid(rfid);
        if (book == null) throw new BookNotFoundException("No book matches RFID: [" + rfid + "].");

        IssueRecord target = null;
        for (IssueRecord r : issueRecords) {
            if (r.getStudentId().equalsIgnoreCase(studentId)
                    && r.getBookId().equalsIgnoreCase(book.getId())
                    && !r.isReturned()) {
                target = r;
                break;
            }
        }

        if (target == null) {
            throw new LibraryException("No active loan found for Student '" + studentId + "' and Book '" + book.getTitle() + "'.");
        }

        LocalDate returnDate = LocalDate.now();
        double fine = FineService.calculate(target.getDueDate(), returnDate);
        target.completeReturn(returnDate, fine);
        book.setAvailable(true);

        if (fine > 0) {
            FineService.printFineReceipt(studentId, book.getId(), target.getDueDate(), returnDate, fine);
        }

        Reservation activated = ReservationService.activateNext(reservations, book.getId());
        if (activated != null) {
            System.out.println("[RESERVATION NOTIFICATION] Book reserved for next queued student ID: " + activated.getStudentId());
        }

        return fine;
    }

    public List<IssueRecord> getHistory(String studentId) {
        List<IssueRecord> list = new ArrayList<>();
        if (studentId == null) return list;
        for (IssueRecord r : issueRecords) {
            if (r.getStudentId().equalsIgnoreCase(studentId.trim())) list.add(r);
        }
        return list;
    }

    public List<IssueRecord> getAllIssueRecords() {
        return issueRecords;
    }

    public List<IssueRecord> getCurrentlyIssuedRecords() {
        List<IssueRecord> list = new ArrayList<>();
        for (IssueRecord r : issueRecords) {
            if (!r.isReturned()) list.add(r);
        }
        return list;
    }

    // =========================================================================
    // RESERVATIONS
    // =========================================================================

    public synchronized Reservation reserveBook(String studentId, String bookId) throws LibraryException {
        Student stu = findStudent(studentId);
        if (stu == null) throw new StudentNotFoundException("Student ID not registered.");

        Book book = findBookById(bookId);
        if (book == null) throw new BookNotFoundException("Book ID does not exist.");

        if (ReservationService.hasActiveReservation(reservations, studentId, bookId)) {
            throw new LibraryException("Student already has an active reservation for this book.");
        }

        String resId = "RES-" + (reservations.size() + 301);
        Reservation res = new Reservation(resId, studentId, stu.getName(), bookId, book.getTitle());
        if (book.isAvailable()) {
            res.setStatus("AVAILABLE");
        }
        reservations.add(res);
        return res;
    }

    public int getReservationPosition(Reservation reservation) {
        return ReservationService.getPositionInQueue(reservations, reservation);
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public synchronized void cancelReservation(String resId, String studentId) throws LibraryException {
        for (Reservation r : reservations) {
            if (r.getReservationId().equalsIgnoreCase(resId)) {
                r.cancel();
                return;
            }
        }
        throw new LibraryException("Reservation ID not found.");
    }

    // =========================================================================
    // FINE SETTLEMENT
    // =========================================================================

    public double getStudentPendingFineTotal(String studentId) {
        double total = 0.0;
        for (IssueRecord r : getHistory(studentId)) {
            if (r.isReturned() && r.getFine() > 0) {
                total += r.getFine();
            }
        }
        return total;
    }

    public synchronized boolean payStudentFine(String studentId) throws LibraryException {
        boolean paid = false;
        for (IssueRecord r : getHistory(studentId)) {
            if (r.isReturned() && r.getFine() > 0) {
                r.completeReturn(r.getReturnDate(), 0.0);
                paid = true;
            }
        }
        if (!paid) throw new LibraryException("No pending fines to settle.");
        return true;
    }

    // =========================================================================
    // AUTHENTICATION
    // =========================================================================

    public Librarian authenticateLibrarian(String username, String password) {
        if (username == null || password == null) return null;
        for (Librarian lib : librarians) {
            if (lib.getId().equalsIgnoreCase(username.trim()) && lib.verifyPassword(password)) {
                return lib;
            }
        }
        return null;
    }

    public Student authenticateStudent(String studentId, String password) {
        if (studentId == null || password == null) return null;
        for (Student stu : students) {
            if (stu.getId().equalsIgnoreCase(studentId.trim()) && stu.verifyPassword(password)) {
                return stu;
            }
        }
        return null;
    }

    // =========================================================================
    // REPORTS (Built-in directly)
    // =========================================================================

    public void printExecutiveSummary() {
        System.out.println("\n============================================================================");
        System.out.println("                 NEXLIB EXECUTIVE AUDIT REPORT");
        System.out.println("============================================================================");

        int totalBooks = books.size();
        int availableBooks = 0;
        for (Book b : books) {
            if (b.isAvailable()) availableBooks++;
        }
        int activeLoans = getCurrentlyIssuedRecords().size();

        double totalFine = 0.0;
        for (IssueRecord r : issueRecords) {
            totalFine += r.getFine();
        }

        System.out.println("Total Catalog Titles     : " + totalBooks);
        System.out.println("Books Available on Shelf : " + availableBooks);
        System.out.println("Books Currently Issued   : " + activeLoans);
        System.out.println("Total Registered Students: " + students.size());
        System.out.println("Total Active Reservations: " + reservations.size());
        System.out.printf("Total Overdue Fines      : Rs. %.2f%n", totalFine);
        System.out.println("----------------------------------------------------------------------------");
    }

    public void printOverdueReport() {
        System.out.println("\n----------------------------------------------------------------------------");
        System.out.println("                         OVERDUE BOOKS AUDIT");
        System.out.println("----------------------------------------------------------------------------");
        boolean found = false;
        for (IssueRecord r : issueRecords) {
            if (!r.isReturned() && r.isOverdue()) {
                found = true;
                System.out.println("Student: " + r.getStudentId() + " (" + r.getStudentName() + ") | Book: " + r.getBookId()
                        + " | Due: " + r.getDueDate() + " | Overdue: " + r.getDaysOverdue() + " days");
            }
        }
        if (!found) {
            System.out.println("No overdue books at present. All loans are on time.");
        }
    }

    public void printCategoryDistribution() {
        System.out.println("\n----------------------------------------------------------------------------");
        System.out.println("                     BOOK CATEGORY DISTRIBUTION");
        System.out.println("----------------------------------------------------------------------------");
        Map<String, Integer> map = new HashMap<>();
        for (Book b : books) {
            map.put(b.getCategory(), map.getOrDefault(b.getCategory(), 0) + 1);
        }
        for (Map.Entry<String, Integer> e : map.entrySet()) {
            System.out.printf("%-30s : %d book(s)%n", e.getKey(), e.getValue());
        }
    }
}
