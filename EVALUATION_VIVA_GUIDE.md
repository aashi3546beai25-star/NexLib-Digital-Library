# NexLib – 3-Stage University Evaluation & Viva Voce Guide
### Course: Programming in Java (25CSE0202) – Chitkara University
**A Comprehensive Evaluation & Viva Voce Guide Aligned with Lectures 13–54 (CA-3 Project Milestone)**

---

## 📌 OVERVIEW: What to Present in Each Evaluation

```
┌────────────────────────────────┐     ┌────────────────────────────────┐     ┌────────────────────────────────┐
│            EVAL 1              │     │            EVAL 2              │     │            EVAL 3              │
│  Design, OOP & Working Core   │ ──> │   Collections & Concurrency    │ ──> │ File Handling, Streams & Demo  │
│ (Lectures 13–15)               │     │ (Lectures 16–33)               │     │ (Lectures 43–54)               │
│ • Problem Statement & Scope    │     │ • List, Set & Map Hierarchies  │     │ • Stream API & Lambda Pipeline │
│ • 6 Layered Packages           │     │ • Iterator & ListIterator      │     │ • Introductory Generics        │
│ • Abstract User -> Student/Lib │     │ • Comparable & Comparators     │     │ • Character Streams (CSV I/O)  │
│ • Custom Exception Hierarchy   │     │ • Multithreading Daemon & Sync │     │ • Byte Streams & Serialization │
│ • Working Menu Prototype       │     │ • Inter-Thread wait()/notify() │     │ • 13-Point Test Suite Pass     │
└────────────────────────────────┘     └────────────────────────────────┘     └────────────────────────────────┘
```

---

## 🟢 EVALUATION 1: Design, OOP Concepts & Basic Prototype (Lectures 13–15)

### 1. Topics Evaluated in Eval 1:
- Problem Statement and Academic Project Scope.
- Class and package structure (`app`, `model`, `service`, `storage`, `util`, `exception`, `test`).
- Core OOP Concepts: Encapsulation, Inheritance, Abstraction, Polymorphism.
- Custom checked exception hierarchy extending `LibraryException`.
- A fully working menu-driven console application.

### 2. Simple Viva Answers to Memorize for Eval 1:

#### Q1: "What is your project about?"
> *"Sir/Ma'am, NexLib is a modular, terminal-based digital college library management system written in standard Core Java without external dependencies. It provides administrative access for librarians, self-service circulation for students, automated overdue fine calculation at Rs. 5/day, background daemon audit monitoring, and file handling with text/CSV import/export."*

#### Q2: "Show me Inheritance in your code."
> *"In package `model`, abstract class `User.java` provides the base identity. Both `Student.java` and `Librarian.java` extend `User`. They inherit common properties like `id`, `name`, and `password`, which eliminates redundancy."*
- **File to open:** `src/model/User.java`, `src/model/Student.java`.

#### Q3: "Show me Abstraction in your code."
> *"Abstraction is implemented in two ways:*
> 1. *Abstract class `User` declares abstract methods `getRole()` and `displayProfile()`, forcing specialized child classes to provide concrete implementations.*
> 2. *Service contracts separate presentation portals (`LibrarianPortal`, `StudentPortal`) from underlying business logic (`LibraryService`), hiding complex internal details."*
- **File to open:** `src/model/User.java`.

#### Q4: "Show me Encapsulation in your code."
> *"In model classes like `Book.java`, fields (`id`, `title`, `author`, `category`, `shelfLocation`, `available`) are declared `private`. Outside components can only read or modify them through public getters and setters that enforce input boundary validation."*
- **File to open:** `src/model/Book.java`.

#### Q5: "Show me Polymorphism in your code."
> *"We implement both compile-time and runtime polymorphism:*
> 1. **Compile-time (Method Overloading):** *`LibraryService.searchBooks(query)` vs `LibraryService.searchBooks(query, category)`; overloaded constructors across `Book.java` and `IssueRecord.java`.*
> 2. **Run-time (Method Overriding):** *`Student` and `Librarian` override `displayProfile()` and `getRole()` dynamically at runtime."*
- **File to open:** `src/service/LibraryService.java`.

#### Q6: "Show me your Custom Exception Hierarchy (Lect 13–15)."
> *"In package `exception`, `LibraryException` is our root checked domain exception extending `Exception`. We defined nested domain exceptions:*
> - `BookNotFoundException`
> - `BookUnavailableException`
> - `DuplicateBookException`
> - `StudentNotFoundException`
> - `InvalidInputException`
> *All exceptions are caught gracefully in menu loops, preventing abnormal termination."*
- **File to open:** `src/exception/LibraryException.java`.

---

## 🟡 EVALUATION 2: Collections Framework & Multithreading (Lectures 16–33)

### 1. Topics Evaluated in Eval 2:
- Collections: `ArrayList`, `LinkedList`, `HashSet`, `TreeSet` (Lect 16–19).
- Maps & Queues: `HashMap`, `TreeMap`, `Queue`, `Iterator`, `ListIterator` (Lect 20–22).
- Sorting: `Comparable<Book>` and custom `Comparator` implementations (Lect 23–25).
- Multithreading: `Thread`, `Runnable`, lifecycle, daemon threads, `sleep()`, `join()`, `interrupt()` (Lect 26–29).
- Synchronization & Intrinsic Locks (Lect 30–31).
- Inter-Thread Communication: `wait()`, `notify()`, `notifyAll()` guarded blocks (Lect 32–33).

### 2. Simple Viva Answers to Memorize for Eval 2:

#### Q1: "Which Collections did you use and WHY (Lect 16–22)?"
> *"We utilized specific collections tailored for optimal performance:*
> 1. **`ArrayList`:** *Stores dynamic records for books, students, and circulation logs.*
> 2. **`HashMap` (`idToBookMap`):** *Provides instant O(1) constant-time book lookups by Book ID or Barcode.*
> 3. **`TreeSet`:** *In `getUniqueCategoriesSorted()`, automatically sorts and deduplicates categories alphabetically.*
> 4. **`HashSet`:** *In `getActiveBorrowerIds()`, maintains an un-duplicated set of student IDs with active loans.*
> 5. **`TreeMap`:** *In `getBooksGroupedByCategory()`, maps category keys to book lists, sorted naturally.*
> 6. **`Queue` (`LinkedList`):** *Maintains FIFO waiting queues for book reservations."*
- **File to open:** `src/service/LibraryService.java` (lines 18–30, 160–210).

#### Q2: "Show me Iterator and ListIterator in your code (Lect 20–22)."
> *"In `LibraryService.java`:*
> 1. *`ListIterator<Book>` in `getBooksReversedWithListIterator()` traverses backwards using `hasPrevious()` and `previous()`.*
> 2. *`Iterator<IssueRecord>` in `purgeOldReturnedRecords()` allows safe removal during list iteration without throwing `ConcurrentModificationException`."*
- **File to open:** `src/service/LibraryService.java` (lines 190–225).

#### Q3: "Explain Comparable vs Comparator in your project (Lect 23–25)."
> *"1. **`Comparable<Book>`:** Implemented in `Book.java` for natural ordering by Book ID (`compareTo(Book other)`).*
> *2. **`Comparator<Book>`:** Defined directly in `Book.java` with multiple sorting strategies (`BY_TITLE`, `BY_AUTHOR`, `BY_CATEGORY`). Users can dynamically select their preferred sort order in the Librarian menu."*
- **File to open:** `src/model/Book.java`.

#### Q4: "Show me Multithreading and Daemon Threads in your code (Lect 26–29)."
> *"In `BackgroundAuditDaemon.java`, we implemented `Runnable`. The daemon thread runs in the background (`setDaemon(true)`) with lower priority (`Thread.NORM_PRIORITY - 1`). It periodically executes audit scans every 10 seconds via `Thread.sleep()`. We can inspect its lifecycle states (`NEW`, `RUNNABLE`, `TIMED_WAITING`, `TERMINATED`), and stop it cleanly using `interrupt()` and `join()`."*
- **File to open:** `src/service/BackgroundAuditDaemon.java`.

#### Q5: "Show me Inter-Thread Communication with wait() and notifyAll() (Lect 32–33)."
> *"In `BookReturnNotifier.java`, we implemented a producer-consumer monitor:*
> - **Consumer (`waitForBook`):** *Calls `monitorLock.wait(timeout)` inside a guarded `while` loop.*
> - **Producer (`signalBookReturned`):** *When a student returns a book in `LibraryService.returnBook()`, `signalBookReturned()` executes `monitorLock.notifyAll()`, waking waiting reservation threads."*
- **File to open:** `src/service/BookReturnNotifier.java`.

---

## 🔴 EVALUATION 3: Stream API, File Handling & Final Defense (Lectures 43–54)

### 1. Topics Evaluated in Eval 3:
- Stream API & Lambdas: Pipelines, `filter()`, `map()`, `sorted()`, `distinct()`, `limit()`, method references (Lect 43–46).
- Introductory Generics: Generic class `DataResult<T>`, type safety, diamond operator `<>`, wildcard bounds `<? extends E>` (Lect 47–48).
- File Handling: Character Streams (`FileReader`, `FileWriter`, `BufferedReader`, `BufferedWriter`, CSV export/import) (Lect 49–54).
- Byte Streams: `FileInputStream`, `FileOutputStream`, Java Object Serialization (`DataStore`) with `.bak` safety (Lect 49–51).
- 13-Point Automated Verification Suite (`test.SystemVerificationTest`).

### 2. Simple Viva Answers to Memorize for Eval 3:

#### Q1: "Show me Stream API and Lambda Expressions in your code (Lect 43–46)."
> *"In `LibraryService.java`, search and analytics leverage Stream pipelines:*
> - `filter()`: `getAvailableBooksStream()` filters books matching `Book::isAvailable`.
> - `map()`: Transforms loan records into fine amounts via `mapToDouble(IssueRecord::getFine)`.
> - `sorted()`: Sorts books dynamically using method references like `Book::getTitle`.
> - `distinct()` & `limit()`: Selects top distinct items."*
- **File to open:** `src/service/LibraryService.java` (lines 125–165).

#### Q2: "Show me Generics in your code (Lect 47–48)."
> *"In `DataResult<T>`, we created a generic response container demonstrating type safety, diamond operators, and a generic utility method `transform(List<? extends E>, Function<E, R>)` using bounded wildcards."*
- **File to open:** `src/util/DataResult.java`.

#### Q3: "How is File Handling and CSV Integration implemented (Lect 49–54)?"
> *"In `CsvDataService.java`:*
> - **Character Streams:** *`FileWriter` & `BufferedWriter` export the book catalog to `books_catalog_export.csv` and write circulation audit logs to text files.*
> - **File Reading & Validation:** *`FileReader` & `BufferedReader` parse imported CSV rows into `Book` objects and integrate them into our Collections.*
> - **Resource Safety:** *All file operations employ `try-with-resources` to ensure streams close safely even if exceptions occur.*
> - **Byte Streams:** *`DataStore.java` uses `FileInputStream` and `FileOutputStream` for persistent binary serialization with atomic `.bak` fail-safe recovery."*
- **File to open:** `src/storage/CsvDataService.java` and `src/storage/DataStore.java`.

#### Q4: "How do you prove that your code satisfies the entire syllabus?"
> *"We created an automated 13-scenario test suite in `src/test/SystemVerificationTest.java`. Running `java -cp out test.SystemVerificationTest` tests all topics from Lecture 13 through Lecture 54, achieving a 100% pass rate."*
- **File to open:** `src/test/SystemVerificationTest.java`.

---

## 🎯 Quick Cheat-Sheet for Live Terminal Demo

1. **Compile all source files:**
   ```powershell
   & "C:\Program Files\Java\jdk-26.0.1\bin\javac.exe" -d out (Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object { $_.FullName })
   ```

2. **Run 13-Point Syllabus Verification Suite:**
   ```powershell
   & "C:\Program Files\Java\jdk-26.0.1\bin\java.exe" -cp out test.SystemVerificationTest
   ```

3. **Run Main Application:**
   ```powershell
   & "C:\Program Files\Java\jdk-26.0.1\bin\java.exe" -cp out app.Main
   ```
   - **Librarian Login:** `admin` / `admin123`
   - **Student Login:** `STU101` / `pass123`
