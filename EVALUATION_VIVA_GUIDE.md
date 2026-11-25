# NexLib – 3-Stage University Evaluation & Viva Voce Guide
### Course: Programming in Java (25CSE0202)
**A Beginner-Friendly Study & Explanation Guide for Eval 1, Eval 2, and Eval 3**

---

## 📌 OVERVIEW: What to Present in Each Evaluation

```
┌────────────────────────────────┐     ┌────────────────────────────────┐     ┌────────────────────────────────┐
│            EVAL 1              │     │            EVAL 2              │     │            EVAL 3              │
│  Design, OOP & Working Core   │ ──> │   Collections & Exceptions     │ ──> │   Persistence & Final Demo     │
│ • Problem Statement & Scope    │     │ • ArrayList, HashMap, Queue    │     │ • Java Object Serialization    │
│ • Package Structure (6 pkgs)   │     │ • RFID Scanning Simulation     │     │ • Fail-safe Backup (.bak)      │
│ • Abstract User -> Student/Lib │     │ • 6 Custom Checked Exceptions  │     │ • Executive Audit Reports      │
│ • Working Prototype Menu       │     │ • Overdue Fine Calculation     │     │ • 13-Point Test Suite Pass     │
└────────────────────────────────┘     └────────────────────────────────┘     └────────────────────────────────┘
```

---

## 🟢 EVALUATION 1: Design, OOP Concepts & Basic Prototype

### 1. What the Examiner Evaluates in Eval 1:
- Problem Statement and Project Scope.
- Class and package structure.
- Implementation of core OOP concepts (Encapsulation, Inheritance, Abstraction, Polymorphism).
- A working menu-driven console prototype.

### 2. Simple Viva Answers to Memorize for Eval 1:

#### Q1: "What is your project about?"
> *"Sir/Ma'am, NexLib is a terminal-based digital college library system. It simulates contactless RFID book checkout and return, manages student records, calculates overdue fines automatically at Rs. 5 per day, and saves all data to disk."*

#### Q2: "Show me Inheritance in your code."
> *"In package `model`, we have an abstract class `User.java`. Both `Student.java` and `Librarian.java` use `extends User`. They inherit common attributes like `id`, `name`, and `password`, which avoids repeating code."*
- **File to open:** `src/model/User.java`, `src/model/Student.java`.

#### Q3: "Show me Abstraction in your code."
> *"We used Abstraction in two places:*
> 1. *Abstract class `User` has abstract methods `getRole()` and `displayProfile()`. Each child class (`Student`, `Librarian`) provides its own specific implementation.*
> 2. *Interface `ILibraryService` defines all the library operations like `addBook()`, `issueBook()`, and `returnBook()`. The UI in `LibraryApp` interacts only with the interface, hiding the complex internal logic."*
- **File to open:** `src/model/User.java` and `src/service/ILibraryService.java`.

#### Q4: "Show me Encapsulation in your code."
> *"In all our model classes like `Book.java`, every field (`id`, `title`, `author`, `available`) is marked `private`. Nobody outside can directly modify them. They can only be accessed or modified through public getters and setters with validation checks."*
- **File to open:** `src/model/Book.java`.

#### Q5: "Show me Polymorphism in your code."
> *"We have both types of Polymorphism:*
> 1. **Compile-time (Method Overloading):** *In `LibraryService.java`, we have two `searchBooks()` methods—one takes a single keyword, and the other takes a keyword plus a category.*
> 2. **Run-time (Method Overriding):** *`Student` and `Librarian` override the `displayProfile()` and `getRole()` methods of `User` using `@Override`."*
- **File to open:** `src/service/LibraryService.java` (lines 190–240).

---

## 🟡 EVALUATION 2: Collections, RFID & Exception Handling

### 1. What the Examiner Evaluates in Eval 2:
- Usage of the Java Collections Framework (`ArrayList`, `HashMap`, `Queue`/`LinkedList`).
- Implementation of custom exception handling.
- Working RFID simulation and business logic (issuing, returning, overdue fines).

### 2. Simple Viva Answers to Memorize for Eval 2:

#### Q1: "Which Collections did you use and WHY?"
> *"We used three standard Java collections, each chosen for a specific reason:*
> 1. **`ArrayList`:** *Used for storing books, students, and issue history because it is an ordered, dynamically resizable list that is easy to iterate over using simple `for` loops.*
> 2. **`HashMap` (`rfidToBookMap`):** *Used for mapping RFID tag strings to `Book` objects. When a book is scanned, the `HashMap` finds the book in instant $O(1)$ constant time instead of searching through a list.*
> 3. **`Queue` (using `LinkedList`):** *Used for book reservations in `ReservationService.java`. When multiple students want the same book, they join a First-In-First-Out (FIFO) queue using `queue.offer()`. The student who reserved first gets the book first when it is returned."*
- **File to open:** `src/service/LibraryService.java` (top fields) and `src/service/ReservationService.java`.

#### Q2: "Show me your Custom Exceptions."
> *"We created 6 custom checked exceptions in the `exception` package, all extending `LibraryException`:*
> - `BookNotFoundException` *(when an ID or RFID does not exist)*
> - `BookUnavailableException` *(when a book is already checked out)*
> - `DuplicateBookException` *(when adding an existing Book ID or RFID)*
> - `StudentNotFoundException` *(when Student ID is not registered)*
> - `InvalidRFIDException` *(when an RFID tag is blank)*
> - `InvalidInputException` *(when input fails validation)*
> *All exceptions are caught with `try-catch` blocks in `LibraryApp.java` so the program never crashes."*
- **File to open:** `src/exception/LibraryException.java`, `src/exception/DuplicateBookException.java`.

#### Q3: "How does your RFID simulation work?"
> *"Every book in `Book.java` has a unique RFID tag like `RFID-CS-101`. In `RFIDScanner.java`, we simulate the hardware antenna. When issuing or returning, the system asks for the tag. It looks up the tag in our `HashMap`. If the tag is valid and the book is available, it checks it out and calculates the due date 7 days later."*
- **File to open:** `src/util/RFIDScanner.java`.

#### Q4: "How is the fine calculated?"
> *"In `FineService.java`, we compare the prescribed due date with the actual return date using `ChronoUnit.DAYS.between(dueDate, returnDate)`. If the return date is after the due date, it multiplies the overdue days by Rs. 5.00 per day."*
- **File to open:** `src/service/FineService.java`.

---

## 🔴 EVALUATION 3: File Persistence, Reporting & Final Defense

### 1. What the Examiner Evaluates in Eval 3:
- Data persistence across application restarts.
- Reliability, error recovery, and transactional backups.
- Executive reporting and statistical audits.
- Full end-to-end working demonstration.

### 2. Simple Viva Answers to Memorize for Eval 3:

#### Q1: "How do you store data without a database like MySQL?"
> *"Sir/Ma'am, our project uses **Java Object Serialization**. All our model classes implement `java.io.Serializable`. In `DataStore.java`, we use `ObjectOutputStream` to write the entire `LibraryService` object directly to a binary file named `nexlib-data.ser`. On startup, `ObjectInputStream` reads it back into memory."*
- **File to open:** `src/storage/DataStore.java`.

#### Q2: "What happens if the application crashes or the computer turns off?"
> *"We implemented two safety mechanisms:*
> 1. **Automatic Backup (`.bak`):** *Before writing to `nexlib-data.ser`, `DataStore.save()` makes a duplicate copy named `nexlib-data.ser.bak`. If the main file ever gets corrupted, it automatically restores from the backup.*
> 2. **JVM Shutdown Hook:** *In `Main.java`, we registered `Runtime.getRuntime().addShutdownHook(...)`. Even if someone closes the terminal or presses Ctrl+C, Java automatically calls `store.save()` before terminating."*
- **File to open:** `src/app/Main.java` and `src/storage/DataStore.java`.

#### Q3: "Show me your Reports and Statistics."
> *"In `ReportService.java`, we have `printExecutiveSummary()` which loops through our lists to calculate total titles, available books, active loans, overdue alerts, and total fines collected. We also have `printCategoryDistribution()` which uses a `HashMap` to count books per department."*
- **File to open:** `src/service/ReportService.java`.

#### Q4: "How do you prove that your project meets all requirements?"
> *"We wrote an automated test suite in `src/test/SystemVerificationTest.java`. We can run it in one command: `java -cp out test.SystemVerificationTest`. It automatically runs all 13 evaluation scenarios and prints `[PASS]` for every single one."*
- **File to open:** `src/test/SystemVerificationTest.java`.

---

## 🎯 Quick Cheat-Sheet for Live Terminal Demo

1. **Compile:**
   ```powershell
   javac -d out (Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object { $_.FullName })
   ```

2. **Run Main Application:**
   ```powershell
   java -cp out app.Main
   ```
   - **Librarian Login:** `admin` / `admin123`
   - **Student Login:** `STU101` / `pass123`

3. **Run 13-Point Automated Test Suite:**
   ```powershell
   java -cp out test.SystemVerificationTest
   ```
