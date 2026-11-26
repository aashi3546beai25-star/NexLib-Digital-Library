# NexLib – Digital College Library Management System

[![Java](https://img.shields.io/badge/Java-SE%2017%2B-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Course](https://img.shields.io/badge/Chitkara%20University-Programming%20in%20Java-red.svg)]()
[![Milestone](https://img.shields.io/badge/Evaluation-CA--3%20Project%20Milestone-blue.svg)]()
[![Build](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()
[![Tests](https://img.shields.io/badge/Syllabus%20Tests-13%2F13%20Passed-brightgreen.svg)]()

> A modular, menu-driven Core Java application developed in strict alignment with the **Chitkara University Programming in Java Course Plan (Lectures 13–54)** for the **Continuous Assessment 3 (CA-3) Project Milestone**.

---

## Table of Contents

1. [Problem Statement](#1-problem-statement)
2. [Introduction](#2-introduction)
3. [Project Objectives](#3-project-objectives)
4. [Syllabus Topic & Technology Alignment](#4-syllabus-topic--technology-alignment)
5. [Complete Package & File Architecture](#5-complete-package--file-architecture)
6. [Core System Modules](#6-core-system-modules)
   - [Librarian Administrative Module](#61-librarian-administrative-module)
   - [Student Self-Service Module](#62-student-self-service-module)
   - [Open Catalog Guest Module](#63-open-catalog-guest-module)
7. [Multithreading & Concurrency Architecture](#7-multithreading--concurrency-architecture)
8. [File Handling & Collections Integration](#8-file-handling--collections-integration)
9. [Compilation & Execution Instructions](#9-compilation--execution-instructions)
10. [Automated 13-Point Syllabus Verification Suite](#10-automated-13-point-syllabus-verification-suite)
11. [Sample Terminal Outputs](#11-sample-terminal-outputs)
12. [Viva Voce & Technical Defense Reference](#12-viva-voce--technical-defense-reference)

---

## 1. Problem Statement

Manual and legacy collegiate library workflows suffer from administrative bottlenecks, human calculation errors during overdue returns, unorganized book reservation waitlists, and fragmented data persistence. 

**NexLib** solves these challenges by providing a robust, zero-dependency Core Java management system featuring:
- Role-based access control for administrative staff and students.
- High-performance catalog lookups using standard Java Collections (`HashMap`, `ArrayList`, `TreeSet`, `TreeMap`).
- Automated overdue penalty computations at Rs. 5.00/day using `java.time`.
- First-In-First-Out (`FIFO`) multi-student reservation waiting queues.
- Real-time background overdue audit scanning via a multithreaded daemon thread.
- Inter-thread producer-consumer synchronization for book return alerts.
- Two-tier persistence: human-readable CSV text import/export via Character Streams (`FileReader`/`FileWriter`) and resilient state serialization via Byte Streams (`FileInputStream`/`FileOutputStream`).

---

## 2. Introduction

**NexLib** is developed exclusively using standard Core Java (JDK 17+) without reliance on third-party frameworks. It emphasizes clean software engineering, design patterns, and standard language features prescribed in the university syllabus:
1. **Librarians:** Catalog management, natural & comparator-based sorting, student registration, circulation issuance/returns, CSV import/export, and background thread monitoring.
2. **Students:** Catalog exploration via Stream API pipelines, self-checkout/return drop simulation, FIFO reservation placement, loan history, and overdue fine settlements.
3. **Guests:** Unrestricted catalog browsing with multi-field search and category filtering.

---

## 3. Project Objectives

- **100% University Syllabus Adherence:** Directly apply concepts taught from Lecture 13 through Lecture 54.
- **Mastery of OOP Principles:** Enforce Abstraction, Encapsulation, Inheritance, and Polymorphism.
- **Effective Collections Selection:** Apply `ArrayList`, `LinkedList`, `HashSet`, `TreeSet`, `HashMap`, and `TreeMap` based on performance trade-offs.
- **Multithreading & Synchronization:** Demonstrate `Thread`, `Runnable`, daemon execution, state transitions, intrinsic locks, and `wait()`/`notifyAll()` monitor coordination.
- **Modern Stream API & Lambdas:** Implement functional pipelines for searching, filtering, mapping, and statistical reporting.
- **Exception-Safe File Handling:** Utilize Character Streams (`BufferedReader`/`BufferedWriter`, `FileReader`/`FileWriter`) for CSV integration and Byte Streams for binary persistence.

---

## 4. Syllabus Topic & Technology Alignment

The following matrix documents the exact mapping of Chitkara University syllabus lectures to the NexLib codebase:

| Lecture Range | Syllabus Topic | Implementation in NexLib | Code Reference |
|---|---|---|---|
| **Lect 13–15** | Strings, Wrappers, Input Validation, Exception Handling | Custom exception hierarchy rooted in `LibraryException`; safe terminal input parsing. | `LibraryException.java`, `Main.java` (`Console`) |
| **Lect 16–19** | Collections Framework: `ArrayList`, `LinkedList`, `HashSet`, `TreeSet` | Dynamic catalog lists (`ArrayList`); queue reservations (`LinkedList`); unique borrower tracking (`HashSet`); alphabetically sorted categories (`TreeSet`). | `LibraryService.java` |
| **Lect 20–22** | `HashMap`, `TreeMap`, `Queue`, `Iterator`, `ListIterator` | $O(1)$ constant-time Book ID indexing (`HashMap`); category-grouped inventory (`TreeMap`); backwards catalog traversal (`ListIterator`); safe element deletion (`Iterator`). | `LibraryService.java` |
| **Lect 23–25** | `Comparable`, `Comparator`, Sorting & Collections Utilities | Natural ordering by Book ID (`Comparable<Book>`); custom sort strategies by Title, Author, Category (`BookComparators`); `Collections.sort()`. | `Models.java` |
| **Lect 26–29** | Multithreading: `Thread`, `Runnable`, States, Daemon Threads, `sleep()`, `join()`, `interrupt()` | Background daemon thread scanning overdue loans every 10 seconds; lifecycle tracking (`NEW`, `RUNNABLE`, `TIMED_WAITING`, `TERMINATED`). | `LibraryService.java` (`BackgroundAuditDaemon`) |
| **Lect 30–31** | Synchronization, Race Conditions, Critical Sections, Intrinsic Locks | Synchronized methods on `LibraryService` protecting shared book and loan collections against concurrent modification. | `LibraryService.java` |
| **Lect 32–33** | Inter-Thread Communication: `wait()`, `notify()`, `notifyAll()`, Guarded Blocks | Producer-consumer monitor waking waiting reservation threads upon book return using guarded `while` loops. | `LibraryService.java` (`BookReturnNotifier`) |
| **Lect 43–46** | Lambda Expressions & Stream API (`filter`, `map`, `sorted`, `distinct`, `limit`) | Functional search pipelines, availability filters, fine aggregations (`mapToDouble`), and method references (`Book::getTitle`). | `LibraryService.java` |
| **Lect 47–48** | Introductory Generics: Generic classes/methods, type safety, diamond operator `<>`, wildcards `<? extends E>` | Generic response container `DataResult<T>` with wildcard list transformation utility. | `Models.java` (`DataResult`) |
| **Lect 49–51** | Byte Streams & Character Streams: `FileInputStream`, `FileOutputStream`, `FileReader`, `FileWriter` | Byte streams for binary object serialization (`LibraryService.save/load`); character streams for CSV import/export (`exportBooksCsv`/`importBooksCsv`). | `LibraryService.java` |
| **Lect 52–54** | File Handling + Collections Integration: Text/CSV read-write, validation, collection import/export | Exporting book collections to CSV; parsing and validating CSV records into `Book` models with try-with-resources. | `LibraryService.java`, `Main.java` |

---

## 5. Complete Package & File Architecture (28 Modular Files)

The project is cleanly divided across 7 decoupled packages with 28 concise, short files:

```text
NexLib_15_Files/
├── EVALUATION_VIVA_GUIDE.md            # Comprehensive viva voce study & preparation guide
├── PROJECT_REPORT.md                   # Full IEEE/University standard academic project report
├── README.md                           # Project technical overview and run instructions
├── nexlib-data.ser                     # Primary persistent binary database
├── out/                                # Compiled JVM bytecode (.class files)
└── src/
    ├── app/
    │   ├── Main.java                   # Entry point with JVM shutdown hook (~20 lines)
    │   ├── LibraryApp.java             # Main menu coordinator (~45 lines)
    │   ├── LibrarianPortal.java        # Administrative console (~100 lines)
    │   ├── StudentPortal.java          # Student self-service circulation (~80 lines)
    │   └── CatalogPortal.java          # Open guest catalog browser (~40 lines)
    ├── exception/
    │   ├── LibraryException.java       # Root domain checked exception (~20 lines)
    │   ├── BookNotFoundException.java  # Thrown when Book ID is missing (~10 lines)
    │   ├── BookUnavailableException.java # Thrown when checking out an issued book (~10 lines)
    │   └── DuplicateBookException.java # Thrown on duplicate book additions (~10 lines)
    ├── model/
    │   ├── User.java                   # Abstract base class demonstrating Abstraction (~25 lines)
    │   ├── Student.java                # Student model inheriting from User (~30 lines)
    │   ├── Librarian.java              # Librarian staff model inheriting from User (~25 lines)
    │   ├── Book.java                   # Encapsulated book implementing Comparable<Book> (~45 lines)
    │   ├── BookComparators.java        # Custom Comparator strategies (Title, Author, Category) (~15 lines)
    │   ├── IssueRecord.java            # Circulation transaction record with fine math (~50 lines)
    │   └── Reservation.java            # FIFO waiting queue model (~30 lines)
    ├── service/
    │   ├── LibraryService.java         # Core business coordinator with Collections & Streams (~130 lines)
    │   ├── BackgroundAuditDaemon.java  # Multithreaded daemon thread for periodic audits (~50 lines)
    │   ├── BookReturnNotifier.java     # Inter-thread synchronization monitor (wait/notifyAll) (~30 lines)
    │   ├── FineService.java            # Overdue fee rules (Rs. 5/day) & ASCII invoice printer (~30 lines)
    │   ├── ReservationService.java     # Queue/LinkedList FIFO manager (~20 lines)
    │   └── ReportService.java          # Executive summary & category distribution (~25 lines)
    ├── storage/
    │   ├── DataStore.java              # Byte Streams serialization (FileInputStream/FileOutputStream) (~40 lines)
    │   └── CsvDataService.java         # Character Streams CSV import/export (FileReader/FileWriter) (~50 lines)
    ├── util/
    │   ├── ConsoleFormatter.java       # ASCII tabular layout formatters (~50 lines)
    │   ├── InputHelper.java            # Safe terminal scanner preventing runtime crashes (~35 lines)
    │   └── DataResult.java             # Generic response container & bounded wildcard utility (~30 lines)
    └── test/
        └── SystemVerificationTest.java # 13-Point Automated University Syllabus Verification Suite (~110 lines)
```

---

## 6. Core System Modules

### 6.1 Librarian Administrative Module
- **Add / Update / Delete Titles:** Add books with catalog ID, title, author, category, shelf rack, and auto-generated barcode.
- **Sorting & Comparators:** View books sorted by Book ID (`Comparable`), Title (`Comparator`), Author, or Category.
- **Circulation Desk:** Issue books to registered students; accept returns with immediate overdue fine assessment.
- **File Handling (CSV Integration):** Export catalog to `books_catalog_export.csv`; import external books via `FileReader`; generate text audit logs.
- **Multithreading Monitor:** Real-time visibility into the `BackgroundAuditDaemon` thread state, priority, and overdue counts.

### 6.2 Student Self-Service Module
- **Self-Checkout & Return Drop:** Borrow and return books independently using Book IDs.
- **FIFO Reservation:** Place a reservation on currently checked-out titles to hold a place in line.
- **Personal Account Dashboard:** View active loans, upcoming return deadlines, full history, and pay outstanding fines.
- **Stream Search:** Execute keyword or category searches powered by Java Stream pipelines.

### 6.3 Open Catalog Guest Module
- Unauthenticated public access for prospective students to browse available titles, view sorted collections, and inspect distinct categories using `TreeSet`.

---

## 7. Multithreading & Concurrency Architecture

1. **Daemon Thread Execution (`BackgroundAuditDaemon.java`):**
   - Implements `Runnable` and executes in the background (`setDaemon(true)`).
   - Set to low priority (`Thread.NORM_PRIORITY - 1`) to ensure zero impact on interactive user commands.
   - Sleeps for 10,000 ms (`Thread.sleep()`), transitioning to `TIMED_WAITING`.
   - Safely interrupted and terminated upon application shutdown using `interrupt()` and `join()`.

2. **Inter-Thread Communication (`BookReturnNotifier.java`):**
   - Coordinates multi-threaded book availability using intrinsic locks on a shared monitor.
   - Consumer threads execute `monitorLock.wait(timeout)` inside guarded `while` loops.
   - When a book is returned in `LibraryService.returnBook()`, `monitorLock.notifyAll()` signals all waiting threads.

---

## 8. File Handling & Collections Integration

1. **Character Streams (`CsvDataService.java`):**
   - **Writing:** Uses `BufferedWriter` wrapping `FileWriter` to generate valid comma-separated values (CSV) with escaped quotation marks.
   - **Reading:** Uses `BufferedReader` wrapping `FileReader` to parse input rows, validate required fields, and populate Collections (`ArrayList`, `HashMap`).
   - **Safety:** All character stream operations are enclosed in `try-with-resources` blocks to ensure leak-free resource release.

2. **Byte Streams (`DataStore.java`):**
   - Persists the entire state using `ObjectOutputStream` wrapping `FileOutputStream`.
   - Automatically maintains a fail-safe `.bak` backup copy prior to disk writes.

---

## 9. Compilation & Execution Instructions

### Prerequisites
- Java Development Kit (**JDK 17** or higher installed).
- Windows PowerShell, Command Prompt, or Linux/macOS Terminal.

### Step 1: Open Terminal in Project Folder
```powershell
cd c:\Users\Asus\Downloads\NexLib_15_Files
```

### Step 2: Compile All 28 Source Files
Compile all Java source files into the `out` directory:
```powershell
& "C:\Program Files\Java\jdk-26.0.1\bin\javac.exe" -d out (Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object { $_.FullName })
```
*(Or standard command if Java is on PATH: `javac -d out (Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object { $_.FullName })`)*

### Step 3: Run the Automated Syllabus Verification Suite
Run the 13-point test suite to confirm complete syllabus compliance:
```powershell
& "C:\Program Files\Java\jdk-26.0.1\bin\java.exe" -cp out test.SystemVerificationTest
```

### Step 4: Run the Interactive Main Application
Launch the terminal application:
```powershell
& "C:\Program Files\Java\jdk-26.0.1\bin\java.exe" -cp out app.Main
```

- **Default Librarian Login:** `admin` / `admin123`
- **Default Student Login:** `STU101` / `pass123` (or `STU102`, `STU103`)

---

## 10. Automated 13-Point Syllabus Verification Suite

Running `java -cp out test.SystemVerificationTest` tests all 13 core syllabus topics:

```text
============================================================================
       NEXLIB - CHITKARA UNIVERSITY SYLLABUS VERIFICATION SUITE
============================================================================
[PASS] Scenario  1: Input Validation & Exception Handling (Lect 13-15)
[PASS] Scenario  2: Collections: ArrayList, LinkedList & Queue (Lect 16-19)
[PASS] Scenario  3: Set Implementations: HashSet & TreeSet (Lect 16-19)
[PASS] Scenario  4: Map Implementations: HashMap & TreeMap (Lect 20-22)
[PASS] Scenario  5: Traversal: Iterator & ListIterator (Lect 20-22)
[PASS] Scenario  6: Comparable Natural Ordering (Lect 23-25)
[PASS] Scenario  7: Comparator Sorting & Utility Methods (Lect 23-25)
[PASS] Scenario  8: Multithreading: Daemon Thread Lifecycle & Sleep (Lect 26-29)
[PASS] Scenario  9: Inter-Thread Communication: wait() & notifyAll() (Lect 32-33)
[PASS] Scenario 10: Stream API: filter, map, sorted, distinct, limit (Lect 43-46)
[PASS] Scenario 11: Introductory Generics: Type Safety & Wildcards (Lect 47-48)
[PASS] Scenario 12: File Handling: Character Streams (CSV Export/Import) (Lect 49-54)
[PASS] Scenario 13: Lending Circulation, Overdue Fines & FIFO Reservation Flow
============================================================================
TEST SUITE RESULTS: 13 / 13 PASSED (100.0% SUCCESS)
============================================================================
[VERDICT] ALL CHITKARA UNIVERSITY SYLLABUS REQUIREMENTS 100% VERIFIED!
```

---

## 11. Sample Terminal Outputs

### Main Menu
```text
============================================================================
  NEXLIB - DIGITAL COLLEGE LIBRARY MANAGEMENT SYSTEM
============================================================================
  1. Librarian Portal (Administrative Access)
  2. Student Portal (Self-Service Circulation)
  3. Browse Catalog & Search Books (Open Guest Access)
  4. Save & Exit
----------------------------------------------------------------------------
Select portal option (1-4): 
```

### Book Catalog View (Natural Ordering)
```text
----------------------------------------------------------------------------
BOOK ID  | TITLE                      | AUTHOR             | CATEGORY        | SHELF        | STATUS   
----------------------------------------------------------------------------
B101     | Data Structures & Algor... | Robert Lafore      | Computer Science | Rack-CS-1    | AVAILABLE
B102     | Operating System Concepts  | Silberschatz & ... | Computer Science | Rack-CS-1    | AVAILABLE
B103     | Digital Logic & Micropr... | M. Morris Mano     | Electronics     | Rack-EC-2    | AVAILABLE
B104     | Higher Engineering Math... | Dr. B. S. Grewal   | Mathematics     | Rack-MA-3    | AVAILABLE
B105     | Fluid Mechanics & Therm... | Frank M. White     | Mechanical      | Rack-ME-4    | AVAILABLE
B106     | Artificial Intelligence... | Russell & Norvig   | Computer Science | Rack-CS-2    | AVAILABLE
----------------------------------------------------------------------------
```

### Overdue Fine Receipt
```text
+-------------------------------------------------------------+
|                   NEXLIB FINE ASSESSMENT RECEIPT            |
+-------------------------------------------------------------+
| Student ID      : STU101                                    |
| Book Catalog ID : B101                                      |
| Prescribed Due  : 2026-11-19                                |
| Actual Return   : 2026-11-26                                |
| Overdue Days    : 7 day(s)                                  |
| Penalty Rate    : Rs. 5.00                                  |
| Total Fine Due  : Rs. 35.00                                 |
+-------------------------------------------------------------+
```

---

## 12. Viva Voce & Technical Defense Reference

For in-depth question-and-answer preparation across all three university evaluation stages, refer to the included guide:
- Comprehensive preparation document: [`EVALUATION_VIVA_GUIDE.md`](file:///c:/Users/Asus/Downloads/NexLib_15_Files/EVALUATION_VIVA_GUIDE.md)
- Complete academic project report: [`PROJECT_REPORT.md`](file:///c:/Users/Asus/Downloads/NexLib_15_Files/PROJECT_REPORT.md)

---
*Developed with pure Core Java for Academic & Engineering Excellence at Chitkara University.*
