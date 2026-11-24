package service;

import exception.*;
import model.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

/**
 * Core business service implementing the ILibraryService interface.
 * Coordinates book inventories, RFID hardware mappings, student registers,
 * circulation loans, reservations, and fine calculations.
 * Demonstrates Collections (ArrayList, HashMap, Queue/LinkedList), OOP Concepts,
 * and comprehensive Custom Exception handling.
 */
public class LibraryService implements ILibraryService, Serializable {
    private static final long serialVersionUID = 1L;

    // ArrayList: Dynamic, indexed list for ordered sequence of books and users
    private List<Book> books = new ArrayList<>();
    private List<Student> students = new ArrayList<>();
    private List<Librarian> librarians = new ArrayList<>();
    private List<IssueRecord> issueRecords = new ArrayList<>();
    private List<Reservation> reservations = new ArrayList<>();
    private List<FineRecord> fineRecords = new ArrayList<>();

    // HashMap: Transient O(1) lookup table mapping unique RFID transponder tags to Book objects
    private transient Map<String, Book> rfidToBookMap = new HashMap<>();

    public LibraryService() {
        rebuildIndices();
    }

    /**
     * Custom deserialization hook to handle schema evolution smoothly.
     */
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        rebuildIndices();
    }

    /**
     * Synchronizes and rebuilds fast lookup maps from serializable lists.
     * Called upon object instantiation and after deserialization.
     */
    public synchronized void rebuildIndices() {
        if (this.books == null) this.books = new ArrayList<>();
        if (this.students == null) this.students = new ArrayList<>();
        if (this.librarians == null) this.librarians = new ArrayList<>();
        if (this.issueRecords == null) this.issueRecords = new ArrayList<>();
        if (this.reservations == null) this.reservations = new ArrayList<>();
        if (this.fineRecords == null) this.fineRecords = new ArrayList<>();

        if (rfidToBookMap == null) {
            rfidToBookMap = new HashMap<>();
        }
        rfidToBookMap.clear();
        for (Book b : books) {
            if (b.getRfidTag() != null && !b.getRfidTag().isEmpty()) {
                rfidToBookMap.put(b.getRfidTag().toUpperCase(), b);
            }
        }

        // Ensure default librarian exists
        if (librarians.isEmpty()) {
            librarians.add(new Librarian("admin", "Dr. S. R. Ranganathan", "admin123", "LIB-001", "Chief Librarian"));
        } else {
            for (Librarian l : librarians) {
                if (l.getPassword() == null || l.getPassword().trim().isEmpty()) {
                    l.setPassword("admin123");
                }
            }
        }

        // Ensure all students have valid passwords set
        for (Student s : students) {
            if (s.getPassword() == null || s.getPassword().trim().isEmpty()) {
                s.setPassword("pass123");
            }
        }
    }

    /**
     * Seeds initial college textbook data, sample students, and circulation entries
     * when the system starts with no previous database.
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
    // BOOK MANAGEMENT IMPLEMENTATION
    // =========================================================================

    @Override
    public synchronized void addBook(Book book) throws LibraryException {
        if (book == null) {
            throw new InvalidInputException("Book details cannot be null.");
        }
        if (book.getId() == null || book.getId().trim().isEmpty()) {
            throw new InvalidInputException("Book ID cannot be empty.");
        }
        if (book.getRfidTag() == null || book.getRfidTag().trim().isEmpty()) {
            throw new InvalidRFIDException("RFID tag is required for every catalog book.");
        }

        if (findBookById(book.getId()) != null) {
            throw new DuplicateBookException("A book with ID '" + book.getId() + "' already exists in the catalog.");
        }

        String tagUpper = book.getRfidTag().toUpperCase();
        if (rfidToBookMap.containsKey(tagUpper)) {
            Book existing = rfidToBookMap.get(tagUpper);
            throw new DuplicateBookException("RFID tag '" + tagUpper + "' is already assigned to book: " + existing.getTitle() + " (" + existing.getId() + ").");
        }

        books.add(book);
        rfidToBookMap.put(tagUpper, book);
    }

    @Override
    public synchronized void updateBook(String bookId, String newTitle, String newAuthor,
                                       String newCategory, String newShelf) throws LibraryException {
        Book book = findBookById(bookId);
        if (book == null) {
            throw new BookNotFoundException("Cannot update. No book found with ID: " + bookId);
        }
        if (newTitle != null && !newTitle.trim().isEmpty()) book.setTitle(newTitle);
        if (newAuthor != null && !newAuthor.trim().isEmpty()) book.setAuthor(newAuthor);
        if (newCategory != null && !newCategory.trim().isEmpty()) book.setCategory(newCategory);
        if (newShelf != null && !newShelf.trim().isEmpty()) book.setShelfLocation(newShelf);
    }

    @Override
    public synchronized void deleteBook(String bookId) throws LibraryException {
        Book book = findBookById(bookId);
        if (book == null) {
            throw new BookNotFoundException("Cannot delete. Book ID '" + bookId + "' was not found in catalog.");
        }

        if (!book.isAvailable()) {
            throw new BookUnavailableException("Cannot delete book '" + book.getTitle() + "' because it is currently issued to a student.");
        }

        // Cancel any pending reservations for this book
        for (Reservation r : reservations) {
            if (r.getBookId().equalsIgnoreCase(bookId) && "WAITING".equalsIgnoreCase(r.getStatus())) {
                r.cancel();
            }
        }

        books.remove(book);
        rfidToBookMap.remove(book.getRfidTag().toUpperCase());
    }

    @Override
    public Book findBookById(String id) {
        if (id == null) return null;
        for (Book book : books) {
            if (book.getId().equalsIgnoreCase(id.trim())) {
                return book;
            }
        }
        return null;
    }

    @Override
    public Book findBookByRfid(String rfid) {
        if (rfid == null) return null;
        return rfidToBookMap.get(rfid.trim().toUpperCase());
    }

    @Override
    public List<Book> getBooks() {
        return Collections.unmodifiableList(books);
    }

    /**
     * General search method matching query across Book ID, Title, Author, or Category.
     */
    @Override
    public List<Book> searchBooks(String query) {
        List<Book> matches = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) return matches;
        String q = query.trim().toLowerCase();

        for (Book book : books) {
            if (book.getId().toLowerCase().contains(q)
                    || book.getTitle().toLowerCase().contains(q)
                    || book.getAuthor().toLowerCase().contains(q)
                    || book.getCategory().toLowerCase().contains(q)
                    || book.getRfidTag().toLowerCase().contains(q)) {
                matches.add(book);
            }
        }
        return matches;
    }

    /**
     * Overloaded search method filtering by keyword within a specific category.
     * Demonstrates OOP Method Overloading.
     */
    @Override
    public List<Book> searchBooks(String query, String category) {
        List<Book> matches = new ArrayList<>();
        if (query == null) query = "";
        String q = query.trim().toLowerCase();
        String cat = category != null ? category.trim().toLowerCase() : "";

        for (Book book : books) {
            boolean matchesCat = cat.isEmpty() || book.getCategory().toLowerCase().contains(cat);
            boolean matchesQuery = q.isEmpty()
                    || book.getTitle().toLowerCase().contains(q)
                    || book.getAuthor().toLowerCase().contains(q)
                    || book.getId().toLowerCase().contains(q);

            if (matchesCat && matchesQuery) {
                matches.add(book);
            }
        }
        return matches;
    }

    // =========================================================================
    // STUDENT MANAGEMENT IMPLEMENTATION
    // =========================================================================

    @Override
    public synchronized void registerStudent(Student student) throws LibraryException {
        if (student == null) {
            throw new InvalidInputException("Student data cannot be null.");
        }
        if (student.getId() == null || student.getId().trim().isEmpty()) {
            throw new InvalidInputException("Student ID is mandatory.");
        }
        if (findStudent(student.getId()) != null) {
            throw new LibraryException("A student with ID '" + student.getId() + "' is already registered.");
        }
        students.add(student);
    }

    @Override
    public Student findStudent(String id) {
        if (id == null) return null;
        for (Student student : students) {
            if (student.getId().equalsIgnoreCase(id.trim())) {
                return student;
            }
        }
        return null;
    }

    @Override
    public List<Student> getStudents() {
        return Collections.unmodifiableList(students);
    }

    // =========================================================================
    // CIRCULATION & RFID LENDING IMPLEMENTATION
    // =========================================================================

    @Override
    public synchronized IssueRecord issueBook(String studentId, String rfid) throws LibraryException {
        Student student = findStudent(studentId);
        if (student == null) {
            throw new StudentNotFoundException("Student ID '" + studentId + "' is not registered in the system.");
        }

        if (rfid == null || rfid.trim().isEmpty()) {
            throw new InvalidRFIDException("RFID scan input cannot be empty.");
        }

        Book book = findBookByRfid(rfid);
        if (book == null) {
            throw new BookNotFoundException("No book is associated with RFID tag: [" + rfid + "].");
        }

        if (!book.isAvailable()) {
            throw new BookUnavailableException("Book '" + book.getTitle() + "' (ID: " + book.getId() + ") is currently checked out.");
        }

        // Verify borrowing limits
        long currentBorrowCount = issueRecords.stream()
                .filter(r -> r.getStudentId().equalsIgnoreCase(studentId) && !r.isReturned())
                .count();

        if (currentBorrowCount >= student.getMaxBooksAllowed()) {
            throw new LibraryException("Student has reached the maximum borrowing limit of "
                    + student.getMaxBooksAllowed() + " books. Please return a book first.");
        }

        // Check if book has a waiting reservation for another student
        Queue<Reservation> waitingQueue = ReservationService.getWaitingQueue(reservations, book.getId());
        if (!waitingQueue.isEmpty()) {
            Reservation top = waitingQueue.peek();
            if (!top.getStudentId().equalsIgnoreCase(studentId)) {
                throw new BookUnavailableException("This book is reserved for student ID '" + top.getStudentId() + "'.");
            } else {
                // This student was next in queue, fulfill reservation
                top.fulfill();
            }
        }

        // Also check if this student had an "AVAILABLE" reservation for this book
        for (Reservation r : reservations) {
            if (r.getBookId().equalsIgnoreCase(book.getId())
                    && r.getStudentId().equalsIgnoreCase(studentId)
                    && "AVAILABLE".equalsIgnoreCase(r.getStatus())) {
                r.fulfill();
                break;
            }
        }

        // Issue book
        book.setAvailable(false);
        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(FineService.STANDARD_BORROW_DAYS);
        String txnId = "TXN-" + String.format("%04d", (issueRecords.size() + 101));

        IssueRecord record = new IssueRecord(txnId, studentId, book.getId(), book.getTitle(), issueDate, dueDate);
        issueRecords.add(record);
        return record;
    }

    @Override
    public synchronized double returnBook(String studentId, String rfid) throws LibraryException {
        Student student = findStudent(studentId);
        if (student == null) {
            throw new StudentNotFoundException("Student ID '" + studentId + "' is not registered.");
        }

        Book book = findBookByRfid(rfid);
        if (book == null) {
            throw new BookNotFoundException("No book found matching RFID tag: [" + rfid + "].");
        }

        // Locate active loan record
        IssueRecord targetRecord = null;
        for (IssueRecord record : issueRecords) {
            if (record.getStudentId().equalsIgnoreCase(studentId)
                    && record.getBookId().equalsIgnoreCase(book.getId())
                    && !record.isReturned()) {
                targetRecord = record;
                break;
            }
        }

        if (targetRecord == null) {
            throw new LibraryException("No active loan record found for Student '" + studentId + "' and Book '" + book.getTitle() + "'.");
        }

        LocalDate returnDate = LocalDate.now();
        double fine = FineService.calculate(targetRecord.getDueDate(), returnDate);

        targetRecord.completeReturn(returnDate, fine);
        book.setAvailable(true);

        // Record fine transaction if overdue
        if (fine > 0) {
            String fineId = "FINE-" + String.format("%04d", (fineRecords.size() + 201));
            fineRecords.add(new FineRecord(fineId, studentId, book.getId(), fine, returnDate));
            FineService.printFineReceipt(studentId, book.getId(), targetRecord.getDueDate(), returnDate, fine);
        }

        // Check if any students are queued in reservations
        Reservation activated = ReservationService.activateNext(reservations, book.getId());
        if (activated != null) {
            Student reservedStudent = findStudent(activated.getStudentId());
            String reservedName = reservedStudent != null ? reservedStudent.getName() : activated.getStudentId();
            System.out.println("\n[RESERVATION NOTIFICATION] Book is now reserved for queued student: "
                    + reservedName + " (ID: " + activated.getStudentId() + ").");
        }

        return fine;
    }

    @Override
    public List<IssueRecord> getHistory(String studentId) {
        List<IssueRecord> history = new ArrayList<>();
        if (studentId == null) return history;

        for (IssueRecord record : issueRecords) {
            if (record.getStudentId().equalsIgnoreCase(studentId.trim())) {
                history.add(record);
            }
        }
        return history;
    }

    @Override
    public List<IssueRecord> getAllIssueRecords() {
        return Collections.unmodifiableList(issueRecords);
    }

    @Override
    public List<IssueRecord> getCurrentlyIssuedRecords() {
        List<IssueRecord> active = new ArrayList<>();
        for (IssueRecord r : issueRecords) {
            if (!r.isReturned()) {
                active.add(r);
            }
        }
        return active;
    }

    // =========================================================================
    // RESERVATION MANAGEMENT IMPLEMENTATION
    // =========================================================================

    @Override
    public synchronized Reservation reserveBook(String studentId, String bookId) throws LibraryException {
        if (findStudent(studentId) == null) {
            throw new StudentNotFoundException("Student ID '" + studentId + "' is not registered.");
        }

        Book book = findBookById(bookId);
        if (book == null) {
            throw new BookNotFoundException("Book ID '" + bookId + "' does not exist in the library catalog.");
        }

        if (ReservationService.hasActiveReservation(reservations, studentId, bookId)) {
            throw new LibraryException("Student already has an active reservation for book: " + book.getTitle());
        }

        String resId = "RES-" + String.format("%04d", (reservations.size() + 301));
        Reservation reservation = new Reservation(resId, studentId, bookId, book.getTitle());

        if (book.isAvailable()) {
            reservation.setStatus("AVAILABLE"); // ready for immediate pickup
        }

        reservations.add(reservation);
        return reservation;
    }

    @Override
    public int getReservationPosition(Reservation reservation) {
        return ReservationService.getPositionInQueue(reservations, reservation);
    }

    @Override
    public List<Reservation> getReservations() {
        return Collections.unmodifiableList(reservations);
    }

    @Override
    public synchronized void cancelReservation(String reservationId, String studentId) throws LibraryException {
        for (Reservation r : reservations) {
            if (r.getReservationId().equalsIgnoreCase(reservationId)
                    && (studentId == null || r.getStudentId().equalsIgnoreCase(studentId))) {
                if ("CANCELLED".equalsIgnoreCase(r.getStatus()) || "FULFILLED".equalsIgnoreCase(r.getStatus())) {
                    throw new LibraryException("Reservation is already " + r.getStatus().toLowerCase() + ".");
                }
                r.cancel();
                return;
            }
        }
        throw new LibraryException("Reservation ID '" + reservationId + "' not found.");
    }

    // =========================================================================
    // FINE MANAGEMENT IMPLEMENTATION
    // =========================================================================

    @Override
    public List<FineRecord> getFineRecords() {
        return Collections.unmodifiableList(fineRecords);
    }

    @Override
    public List<FineRecord> getStudentFineRecords(String studentId) {
        List<FineRecord> list = new ArrayList<>();
        if (studentId == null) return list;
        for (FineRecord f : fineRecords) {
            if (f.getStudentId().equalsIgnoreCase(studentId.trim())) {
                list.add(f);
            }
        }
        return list;
    }

    @Override
    public double getStudentPendingFineTotal(String studentId) {
        double total = 0.0;
        for (FineRecord f : getStudentFineRecords(studentId)) {
            if (!f.isPaid()) {
                total += f.getAmount();
            }
        }
        return total;
    }

    @Override
    public synchronized boolean payStudentFine(String studentId) throws LibraryException {
        Student student = findStudent(studentId);
        if (student == null) {
            throw new StudentNotFoundException("Student ID '" + studentId + "' not registered.");
        }

        List<FineRecord> list = getStudentFineRecords(studentId);
        boolean settledAny = false;
        for (FineRecord f : list) {
            if (!f.isPaid()) {
                f.markAsPaid(LocalDate.now());
                settledAny = true;
            }
        }

        if (!settledAny) {
            throw new LibraryException("No pending fines found for student: " + student.getName());
        }
        return true;
    }

    // =========================================================================
    // AUTHENTICATION IMPLEMENTATION
    // =========================================================================

    @Override
    public Librarian authenticateLibrarian(String username, String password) {
        if (username == null || password == null) return null;
        for (Librarian lib : librarians) {
            if ((lib.getId().equalsIgnoreCase(username.trim()) || lib.getEmployeeCode().equalsIgnoreCase(username.trim()))
                    && lib.verifyPassword(password)) {
                return lib;
            }
        }
        return null;
    }

    @Override
    public Student authenticateStudent(String studentId, String password) {
        if (studentId == null || password == null) return null;
        for (Student stu : students) {
            if (stu.getId().equalsIgnoreCase(studentId.trim()) && stu.verifyPassword(password)) {
                return stu;
            }
        }
        return null;
    }
}
