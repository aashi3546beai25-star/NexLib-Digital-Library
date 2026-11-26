package service;

import exception.LibraryException;
import exception.LibraryException.*;
import model.*;

import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Main business service coordinating books, students, circulation loans, and reservations.
 */
public class LibraryService implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final double FINE_PER_DAY = 5.0;

    private List<Book> books = new ArrayList<>();
    private List<Student> students = new ArrayList<>();
    private List<Librarian> librarians = new ArrayList<>();
    private List<IssueRecord> issueRecords = new ArrayList<>();
    private List<Reservation> reservations = new ArrayList<>();

    private transient Map<String, Book> idMap = new HashMap<>();
    private transient BackgroundAuditDaemon auditDaemon;
    private transient BookReturnNotifier returnNotifier = new BookReturnNotifier();

    public LibraryService() { init(); }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        init();
    }

    public synchronized void init() {
        if (books == null) books = new ArrayList<>();
        if (students == null) students = new ArrayList<>();
        if (librarians == null) librarians = new ArrayList<>();
        if (issueRecords == null) issueRecords = new ArrayList<>();
        if (reservations == null) reservations = new ArrayList<>();

        if (idMap == null) idMap = new HashMap<>();
        idMap.clear();
        for (Book b : books) idMap.put(b.getId().toUpperCase(), b);

        if (librarians.isEmpty()) librarians.add(new Librarian("admin", "Chief Librarian", "admin123", "Head"));
        if (returnNotifier == null) returnNotifier = new BookReturnNotifier();
        if (auditDaemon == null) {
            auditDaemon = new BackgroundAuditDaemon(this, 10000);
            auditDaemon.start();
        }
    }

    public void seedInitialData() {
        if (books.isEmpty()) {
            try {
                addBook(new Book("B101", "Data Structures in Java", "Robert Lafore", "Computer Science", "Rack-CS-1"));
                addBook(new Book("B102", "Operating System Concepts", "Silberschatz", "Computer Science", "Rack-CS-1"));
                addBook(new Book("B103", "Digital Logic Design", "Morris Mano", "Electronics", "Rack-EC-2"));
                addBook(new Book("B104", "Engineering Mathematics", "B.S. Grewal", "Mathematics", "Rack-MA-3"));
                addBook(new Book("B105", "Fluid Mechanics", "Frank White", "Mechanical", "Rack-ME-4"));
                addBook(new Book("B106", "Artificial Intelligence", "Russell & Norvig", "Computer Science", "Rack-CS-2"));
            } catch (LibraryException ignored) {}
        }
        if (students.isEmpty()) {
            try {
                registerStudent(new Student("STU101", "Aarav Sharma", "Computer Science"));
                registerStudent(new Student("STU102", "Diya Patel", "Electronics"));
                registerStudent(new Student("STU103", "Rohan Verma", "Mechanical"));
            } catch (LibraryException ignored) {}
        }
    }

    public synchronized void addBook(Book b) throws LibraryException {
        if (b == null || b.getId().isEmpty()) throw new InvalidInputException("Book cannot be null or empty.");
        if (idMap.containsKey(b.getId().toUpperCase())) throw new DuplicateBookException("Book ID already exists: " + b.getId());
        books.add(b);
        idMap.put(b.getId().toUpperCase(), b);
    }

    public synchronized void deleteBook(String id) throws LibraryException {
        Book b = findBook(id);
        if (b == null) throw new BookNotFoundException("Book not found: " + id);
        if (!b.isAvailable()) throw new BookUnavailableException("Cannot delete currently checked out book.");
        books.remove(b);
        idMap.remove(b.getId().toUpperCase());
    }

    public Book findBook(String id) { return id != null ? idMap.get(id.trim().toUpperCase()) : null; }
    public List<Book> getBooks() { return books; }

    public synchronized void registerStudent(Student s) throws LibraryException {
        if (s == null || s.getId().isEmpty()) throw new InvalidInputException("Student details invalid.");
        if (findStudent(s.getId()) != null) throw new LibraryException("Student ID already exists: " + s.getId());
        students.add(s);
    }

    public Student findStudent(String id) {
        if (id == null) return null;
        for (Student s : students) if (s.getId().equalsIgnoreCase(id.trim())) return s;
        return null;
    }
    public List<Student> getStudents() { return students; }

    public Queue<Reservation> getReservationQueue(String bookId) {
        Queue<Reservation> q = new LinkedList<>();
        for (Reservation r : reservations) {
            if (r.getBookId().equalsIgnoreCase(bookId) && "WAITING".equals(r.getStatus())) {
                q.offer(r);
            }
        }
        return q;
    }

    public static double calculateFine(LocalDate due, LocalDate ret) {
        if (due == null || ret == null) return 0.0;
        long days = Math.max(0, ChronoUnit.DAYS.between(due, ret));
        return days * FINE_PER_DAY;
    }

    public static void printFineReceipt(String stuId, String bookId, LocalDate due, LocalDate ret, double fine) {
        System.out.println("\n+-------------------------------------------------------------+");
        System.out.println("|                   NEXLIB OVERDUE FINE RECEIPT              |");
        System.out.println("+-------------------------------------------------------------+");
        System.out.printf("| Student ID : %-47s |%n", stuId);
        System.out.printf("| Book ID    : %-47s |%n", bookId);
        System.out.printf("| Due Date   : %-47s |%n", due);
        System.out.printf("| Return Date: %-47s |%n", ret);
        System.out.printf("| Overdue Day: %-47d |%n", Math.max(0, ChronoUnit.DAYS.between(due, ret)));
        System.out.printf("| Total Fine : Rs. %-43.2f |%n", fine);
        System.out.println("+-------------------------------------------------------------+");
    }

    public synchronized IssueRecord issueBook(String stuId, String bookId) throws LibraryException {
        Student stu = findStudent(stuId);
        if (stu == null) throw new StudentNotFoundException("Student ID not found: " + stuId);
        Book book = findBook(bookId);
        if (book == null) throw new BookNotFoundException("Book not found: " + bookId);
        if (!book.isAvailable()) throw new BookUnavailableException("Book is already checked out: " + book.getTitle());

        long count = issueRecords.stream().filter(r -> r.getStudentId().equalsIgnoreCase(stuId) && !r.isReturned()).count();
        if (count >= stu.getMaxBooks()) throw new LibraryException("Borrow limit reached: " + stu.getMaxBooks() + " books.");

        Queue<Reservation> q = getReservationQueue(book.getId());
        if (!q.isEmpty()) {
            Reservation top = q.peek();
            if (!top.getStudentId().equalsIgnoreCase(stuId)) throw new BookUnavailableException("Reserved for student: " + top.getStudentId());
            top.setStatus("FULFILLED");
        }

        book.setAvailable(false);
        LocalDate now = LocalDate.now();
        IssueRecord r = new IssueRecord("TXN-" + (issueRecords.size() + 101), stu.getId(), stu.getName(), book.getId(), book.getTitle(), now, now.plusDays(7));
        issueRecords.add(r);
        if (auditDaemon != null) auditDaemon.scan();
        return r;
    }

    public synchronized double returnBook(String stuId, String bookId) throws LibraryException {
        Student stu = findStudent(stuId);
        if (stu == null) throw new StudentNotFoundException("Student not found.");
        Book book = findBook(bookId);
        if (book == null) throw new BookNotFoundException("Book not found.");

        IssueRecord r = issueRecords.stream()
                .filter(rec -> rec.getStudentId().equalsIgnoreCase(stuId) && rec.getBookId().equalsIgnoreCase(book.getId()) && !rec.isReturned())
                .findFirst().orElseThrow(() -> new LibraryException("No active loan found."));

        LocalDate today = LocalDate.now();
        double fine = calculateFine(r.getDueDate(), today);
        r.completeReturn(today, fine);
        book.setAvailable(true);

        if (fine > 0) printFineReceipt(stuId, bookId, r.getDueDate(), today, fine);
        if (returnNotifier != null) returnNotifier.signalBookReturned(book.getId());

        Queue<Reservation> q = getReservationQueue(book.getId());
        if (!q.isEmpty()) {
            Reservation next = q.peek();
            next.setStatus("AVAILABLE");
            System.out.println("RESERVATION ALERT: Book reserved for next queued student: " + next.getStudentId());
        }
        if (auditDaemon != null) auditDaemon.scan();
        return fine;
    }

    public synchronized Reservation reserveBook(String stuId, String bookId) throws LibraryException {
        Student stu = findStudent(stuId);
        if (stu == null) throw new StudentNotFoundException("Student not registered.");
        Book book = findBook(bookId);
        if (book == null) throw new BookNotFoundException("Book not found.");

        boolean exists = reservations.stream().anyMatch(r -> r.getStudentId().equalsIgnoreCase(stuId) && r.getBookId().equalsIgnoreCase(bookId) && !"FULFILLED".equals(r.getStatus()));
        if (exists) throw new LibraryException("Active reservation already exists.");

        Reservation res = new Reservation("RES-" + (reservations.size() + 201), stuId, stu.getName(), book.getId(), book.getTitle());
        if (book.isAvailable()) res.setStatus("AVAILABLE");
        reservations.add(res);
        return res;
    }

    public List<IssueRecord> getHistory(String stuId) {
        return issueRecords.stream().filter(r -> r.getStudentId().equalsIgnoreCase(stuId)).collect(Collectors.toList());
    }
    public List<IssueRecord> getAllLoans() { return issueRecords; }
    public List<Reservation> getReservations() { return reservations; }

    public double getStudentPendingFineTotal(String stuId) {
        return getHistory(stuId).stream().filter(r -> r.isReturned() && r.getFine() > 0).mapToDouble(IssueRecord::getFine).sum();
    }
    public synchronized boolean payStudentFine(String stuId) throws LibraryException {
        List<IssueRecord> dues = getHistory(stuId).stream().filter(r -> r.isReturned() && r.getFine() > 0).toList();
        if (dues.isEmpty()) throw new LibraryException("No pending fines.");
        dues.forEach(r -> r.completeReturn(r.getReturnDate(), 0.0));
        return true;
    }

    public Set<String> getUniqueCategoriesSorted() {
        Set<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        books.forEach(b -> set.add(b.getCategory()));
        return set;
    }
    public Set<String> getActiveBorrowerIds() {
        return issueRecords.stream().filter(r -> !r.isReturned()).map(IssueRecord::getStudentId).collect(Collectors.toCollection(HashSet::new));
    }
    public Map<String, List<Book>> getBooksGroupedByCategory() {
        Map<String, List<Book>> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        books.forEach(b -> map.computeIfAbsent(b.getCategory(), k -> new ArrayList<>()).add(b));
        return map;
    }
    public List<Book> getBooksReversedWithListIterator() {
        List<Book> rev = new ArrayList<>();
        ListIterator<Book> it = books.listIterator(books.size());
        while (it.hasPrevious()) rev.add(it.previous());
        return rev;
    }
    public int purgeOldReturnedRecords(int days) {
        int c = 0;
        Iterator<IssueRecord> it = issueRecords.iterator();
        while (it.hasNext()) {
            IssueRecord r = it.next();
            if (r.isReturned() && ChronoUnit.DAYS.between(r.getReturnDate(), LocalDate.now()) > days) {
                it.remove();
                c++;
            }
        }
        return c;
    }
    public List<Book> getSortedBooks(Comparator<Book> comp) {
        return books.stream().sorted(comp).collect(Collectors.toList());
    }

    public List<Book> getAvailableBooksStream() {
        return books.stream().filter(Book::isAvailable).collect(Collectors.toList());
    }
    public List<Book> searchBooks(String q, String cat) {
        String query = q != null ? q.trim().toLowerCase() : "";
        String category = cat != null ? cat.trim().toLowerCase() : "";
        return books.stream()
                .filter(b -> category.isEmpty() || b.getCategory().toLowerCase().contains(category))
                .filter(b -> query.isEmpty() || b.getId().toLowerCase().contains(query)
                        || b.getTitle().toLowerCase().contains(query) || b.getAuthor().toLowerCase().contains(query))
                .collect(Collectors.toList());
    }

    public Librarian authLibrarian(String u, String p) {
        return librarians.stream().filter(l -> l.getId().equalsIgnoreCase(u) && l.verifyPassword(p)).findFirst().orElse(null);
    }
    public Student authStudent(String id, String p) {
        return students.stream().filter(s -> s.getId().equalsIgnoreCase(id) && s.verifyPassword(p)).findFirst().orElse(null);
    }

    public BackgroundAuditDaemon getAuditDaemon() { return auditDaemon; }
    public BookReturnNotifier getReturnNotifier() { return returnNotifier; }
}
