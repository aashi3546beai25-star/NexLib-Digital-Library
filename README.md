# NexLib – Digital College Library & RFID Book Lending System

[![Java](https://img.shields.io/badge/Java-SE%2017%2B-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![License](https://img.shields.io/badge/License-Academic%20Evaluation-blue.svg)](LICENSE)
[![Build](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()
[![Platform](https://img.shields.io/badge/Platform-Cross--Platform-lightgrey.svg)]()

> A robust, menu-driven, terminal-based Core Java application demonstrating Object-Oriented Programming (OOP) paradigms, Collections Framework, Custom Exception Hierarchies, Hardware-simulated RFID Book Lending, Automatic Overdue Fine Calculation, and Persistent File Serialization.

---

## Table of Contents

1. [Problem Statement](#1-problem-statement)
2. [Introduction](#2-introduction)
3. [Project Objectives](#3-project-objectives)
4. [Project Scope](#4-project-scope)
5. [Technologies & Tools Used](#5-technologies--tools-used)
6. [Java OOP & Core Concepts Implemented](#6-java-oop--core-concepts-implemented)
7. [Features and Modules](#7-features-and-modules)
   - [Librarian Module](#71-librarian-module)
   - [Student Module](#72-student-module)
   - [RFID Lending Simulation](#73-rfid-lending-simulation)
   - [Fine Management](#74-fine-management)
   - [Reporting & Audits](#75-reporting--audits)
8. [Complete Project Structure](#8-complete-project-structure)
9. [Compilation & Execution Instructions](#9-compilation--execution-instructions)
10. [Sample Terminal Outputs](#10-sample-terminal-outputs)
11. [Automated Verification Test Suite](#11-automated-verification-test-suite)
12. [Future Scope](#12-future-scope)

---

## 1. Problem Statement

Traditional collegiate libraries frequently struggle with manual record keeping, human latency during check-in/check-out periods, inaccurate overdue fine assessments, and disjointed reservation queues. Physical barcodes require optical line-of-sight and are easily damaged. 

**NexLib** solves these operational bottlenecks by simulating an intelligent **Radio-Frequency Identification (RFID)** circulation kiosk coupled with a Core Java architecture that enforces strict business rules, automated fine computation, multi-tier queue management, and state persistence across application restarts.

---

## 2. Introduction

**NexLib – Digital College Library & RFID Book Lending System** is designed as a production-grade, terminal-based enterprise academic project. Built strictly in standard Core Java without heavy external GUI or web frameworks, it provides an intuitive command-line interface (CLI) with clean ASCII tables, input validation, role-based authentication, and transaction safety.

The application serves two primary user categories:
1. **Librarians (Staff):** Manage book inventory, register students, monitor overdue loans, oversee reservations, collect fines, and generate statistical audit reports.
2. **Students:** Browse catalog holdings, self-issue and self-return books via RFID simulation, view due dates, join waiting queues for reserved books, and track pending fines.

---

## 3. Project Objectives

- **Simulate RFID Hardware Circulation:** Implement high-frequency RFID transponder tag verification for contactless borrowing and returns.
- **Demonstrate OOP Mastery:** Embody Abstraction, Encapsulation, Inheritance, Polymorphism, Method Overloading/Overriding, and Interface segregation.
- **Utilize Standard Java Collections:** Effectively leverage `ArrayList`, `HashMap`, and `Queue` (`LinkedList`) for fast lookups, dynamic records, and FIFO reservation queues.
- **Enforce Custom Exception Handling:** Create an organized exception hierarchy to prevent runtime crashes caused by invalid inputs or state violations.
- **Ensure High-Reliability Data Persistence:** Utilize Java Object Serialization with automatic backup generation (`.bak`) and index rebuilding.
- **Automate Financial Penalties:** Calculate per-day fines based on actual dates versus prescribed due dates.

---

## 4. Project Scope

- **In-Scope:**
  - Complete menu-driven CLI running on Windows, macOS, and Linux terminal environments.
  - Multi-user authentication (Librarian and Student portals).
  - Search engine supporting general keywords, IDs, authors, titles, and category filters.
  - Overdue loan tracking with grace period and fine policy enforcement.
  - Multi-student reservation waiting lists prioritizing earlier reservations upon book returns.
  - Automated testing suite verifying all core university evaluation scenarios.
- **Out-of-Scope (By Design):**
  - External graphical user interfaces (Swing, JavaFX), web servers, or Android wrappers, maintaining focus on pure Core Java fundamentals.

---

## 5. Technologies & Tools Used

| Component | Specification |
|---|---|
| **Language** | Java SE 17+ (Tested on Java 21 / Java 26) |
| **Development Paradigm** | Object-Oriented Programming (OOP) & Clean Architecture |
| **Collections Framework** | `ArrayList`, `HashMap`, `LinkedList`, `Queue`, `List`, `Map` |
| **File I/O & Persistence** | Java Object Serialization (`ObjectInputStream`, `ObjectOutputStream`) |
| **Date Time API** | Modern `java.time` (`LocalDate`, `ChronoUnit`) |
| **Terminal Formatting** | Native ASCII table layouts, bordered menus, dynamic padding |
| **IDE Compatibility** | VS Code, IntelliJ IDEA, Eclipse, NetBeans, Terminal |

---

## 6. Java OOP & Core Concepts Implemented

| Concept | Implementation in NexLib | File Reference |
|---|---|---|
| **Classes & Objects** | Models real-world entities (`Book`, `Student`, `Librarian`, `IssueRecord`, `Reservation`). | `src/model/*.java` |
| **Encapsulation** | Private instance variables with public accessors/mutators; validation logic inside setters. | `Book.java`, `User.java` |
| **Inheritance** | Single-table class hierarchy where `Student` and `Librarian` inherit common identity from `User`. | `User.java`, `Student.java`, `Librarian.java` |
| **Abstraction** | Abstract class `User` declares abstract `getRole()` and `displayProfile()`. | `User.java` |
| **Polymorphism** | Dynamic method dispatch: calling `user.displayProfile()` executes role-specific rendering at runtime. | `LibraryApp.java`, `User.java` |
| **Method Overloading** | Overloaded `searchBooks(query)` vs `searchBooks(query, category)`; overloaded `FineService.calculate()`; overloaded constructors. | `LibraryService.java`, `FineService.java` |
| **Method Overriding** | `@Override` used in subclasses for `getRole()`, `displayProfile()`, and across all models for `toString()`. | `Student.java`, `Book.java` |
| **Constructors** | Parameterized constructors, default constructors, and constructor chaining using `this(...)` and `super(...)`. | `Student.java`, `Book.java` |
| **Static Members** | Static constants (`FINE_PER_DAY`, `STANDARD_BORROW_DAYS`), static methods in utility and service classes. | `FineService.java`, `InputHelper.java` |
| **Collections** | `ArrayList` for dynamic records; `HashMap` for O(1) RFID lookup; `Queue`/`LinkedList` for FIFO reservation queues. | `LibraryService.java`, `ReservationService.java` |
| **Custom Exceptions** | Hierarchical checked exceptions extending `LibraryException` to handle domain-specific failures. | `src/exception/LibraryException.java` |

---

## 7. Features and Modules

### 7.1 Librarian Module
- **Secure Authentication:** Username and password authentication with lockout safeguards (Default: `admin` / `admin123`).
- **Book Inventory Management:** Add new books with auto-generated or custom RFID tags, update metadata, and safely delete books (with checkout locks).
- **Student Directory:** Register students with department, phone, email, and borrow limit; view student records.
- **Circulation Management:** Issue books using terminal-simulated RFID scanning; accept returns with instant fine computation.
- **Auditing:** View currently issued books, view complete loan history across students, and monitor reservation queues.
- **Fine Collection:** Inspect outstanding dues and mark fine receipts as settled.
- **Analytical Reports:** Generate Executive Summaries, Overdue Audits, and Category Distribution graphs.

### 7.2 Student Module
- **Self-Service Authentication:** Login with Student ID and password (e.g., `STU101` / `pass123`).
- **Catalog Exploration:** View available titles in real time and execute multi-field searches.
- **Self-Checkout Kiosk:** Borrow books independently using simulated RFID transponders.
- **Return Drop Box:** Return books independently with immediate notification of fine assessments.
- **Reservation Placement:** Reserve checked-out books and receive an assigned queue position.
- **Personal Dashboard:** Check currently held books, due dates, loan history, and pending fines.

### 7.3 RFID Lending Simulation
Every physical book in the library catalog is tagged with a unique 13.56 MHz RFID transponder tag (e.g., `RFID-CS-101`). During check-out or return, the system triggers the simulated RFID reader:
1. Prompts user to scan or enter tag ID.
2. Identifies matching book in $O(1)$ time via `HashMap<String, Book>`.
3. Verifies book availability and loan eligibility.
4. Updates asset state and creates an immutable circulation log.

### 7.4 Fine Management
- **Prescribed Borrow Period:** 7 Days (`STANDARD_BORROW_DAYS`).
- **Penalty Rate:** Rs. 5.00 per overdue day (`FINE_PER_DAY`).
- **Calculation Formula:**
  $$\text{Overdue Days} = \max(0, \text{ChronoUnit.DAYS.between}(\text{DueDate}, \text{ReturnDate}))$$
  $$\text{Total Fine} = \text{Overdue Days} \times \text{FINE\_PER\_DAY}$$
- Clear terminal invoice breakdown showing due date, actual return date, overdue days, and total fine.

### 7.5 Reporting & Audits
- **Executive Summary:** Total titles, circulating inventory, available count, registered students, active loans, overdue alerts, and total fines assessed vs. collected.
- **Overdue Audit:** Real-time list of all overdue loans with borrower name, contact ID, and accumulated days late.
- **Category Inventory:** Grouped book counts across disciplines (Computer Science, Electronics, Mechanical, Mathematics, etc.).

---

## 8. Complete Project Structure

```
NexLib_15_Files/
├── .gitignore                          # Git tracking exclusions
├── nexlib-data.ser                     # Serialized persistent database file
├── nexlib-data.ser.bak                 # Automatic fail-safe backup file
├── README.md                           # Master project documentation
├── PROJECT_REPORT.md                   # University academic evaluation report
├── EVALUATION_VIVA_GUIDE.md            # Comprehensive viva preparation guide
├── out/                                # Compiled Java bytecode (.class files)
└── src/                                # Core Java Source Code (22 modular files)
    ├── app/
    │   ├── Main.java                   # Application entry point with shutdown hooks
    │   ├── LibraryApp.java             # Lightweight main menu router (~50 lines)
    │   ├── LibrarianPortal.java        # Dedicated Librarian console (6 core options)
    │   ├── StudentPortal.java          # Dedicated Student self-service circulation
    │   └── CatalogPortal.java          # Guest catalog browser & viva demonstration
    ├── exception/
    │   ├── LibraryException.java       # Base checked domain exception
    │   ├── BookNotFoundException.java  # Thrown when Book ID or RFID is not found
    │   └── BookUnavailableException.java# Thrown when book is already checked out
    ├── model/
    │   ├── User.java                   # Abstract base class for all system users
    │   ├── Student.java                # College student entity extending User
    │   ├── Librarian.java              # Library administrator entity extending User
    │   ├── Book.java                   # Physical book asset with RFID tag
    │   ├── IssueRecord.java            # Circulation loan transaction record
    │   └── Reservation.java            # Queue reservation record
    ├── service/
    │   ├── LibraryService.java         # Core business logic & storage coordinator
    │   ├── FineService.java            # Static fine calculations and overdue rules
    │   ├── ReservationService.java     # Queue/LinkedList FIFO waiting list manager
    │   └── ReportService.java          # Administrative statistical audit reports
    ├── storage/
    │   └── DataStore.java              # Resilient serialization and backup engine
    └── util/
        ├── ConsoleFormatter.java       # ASCII table formatters and section banners
        ├── InputHelper.java            # Safe terminal scanner preventing input crashes
        └── RFIDScanner.java            # Terminal-simulated RFID hardware transponder
```

---

## 9. Compilation & Execution Instructions

### Prerequisites
- Java Development Kit (**JDK 17** or higher installed).
- Terminal (Windows PowerShell, Command Prompt, or Bash).

### Step 1: Open Terminal in Project Folder
```powershell
cd c:\Users\Asus\Downloads\NexLib_15_Files
```

### Step 2: Compile the 22 Source Files
Compile all Java source files into the `out` directory:

**Windows PowerShell:**
```powershell
& "C:\Program Files\Java\jdk-26.0.1\bin\javac.exe" -d out (Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object { $_.FullName })
```
*(Or standard command if Java is on PATH: `javac -d out src/app/*.java src/model/*.java src/service/*.java src/storage/*.java src/util/*.java src/exception/*.java`)*

### Step 3: Run the Application
Launch the terminal application:
```powershell
& "C:\Program Files\Java\jdk-26.0.1\bin\java.exe" -cp out app.Main
```
*(Or standard: `java -cp out app.Main`)*

---

## 10. Sample Terminal Outputs

### Main Menu
```
============================================================================
        NEXLIB - DIGITAL COLLEGE LIBRARY & RFID BOOK LENDING SYSTEM
============================================================================
  1. Librarian Portal (Administrative Access)
  2. Student Portal (Self-Service Circulation)
  3. Browse Catalog & Search Books (Open Guest Access)
  4. Architecture & Viva Demonstration Guide
  5. Save & Exit
----------------------------------------------------------------------------
Select portal option (1-5): 
```

### Book Catalog View
```
----------------------------------------------------------------------------
                            LIBRARY BOOK CATALOG
----------------------------------------------------------------------------
BOOK ID  | TITLE                      | AUTHOR             | CATEGORY        | RFID TAG     | STATUS   
----------------------------------------------------------------------------
B101     | Data Structures & Algor... | Robert Lafore      | Computer Science | RFID-CS-101  | AVAILABLE
B102     | Operating System Concepts  | Silberschatz & ... | Computer Science | RFID-CS-102  | AVAILABLE
B103     | Digital Logic & Micropr... | M. Morris Mano     | Electronics     | RFID-EC-201  | AVAILABLE
B104     | Higher Engineering Math... | Dr. B. S. Grewal   | Mathematics     | RFID-MA-301  | AVAILABLE
B105     | Fluid Mechanics & Therm... | Frank M. White     | Mechanical      | RFID-ME-401  | AVAILABLE
B106     | Artificial Intelligence... | Russell & Norvig   | Computer Science | RFID-CS-106  | AVAILABLE

Total Titles in Library: 6
```

### RFID Simulated Scan & Book Issue
```
>>> [RFID HARDWARE EMULATOR] 13.56 MHz RFID Transponder Reader
>>> Place book on RFID Reader Pad or enter tag ID: RFID-CS-101
>>> [RFID BEEP!] Captured Tag UID: [RFID-CS-101]

[SUCCESS] BOOK ISSUED SUCCESSFULLY!
Transaction ID : TXN-0101
Book Title     : Data Structures & Algorithms in Java (ID: B101)
Issued Date    : 2026-11-24
Due Date       : 2026-12-01 (Period: 7 days)
Fine Policy    : Rs. 5.0 per day for overdue returns.
```

### Overdue Fine Receipt
```
+-------------------------------------------------------------+
|                   NEXLIB FINE ASSESSMENT RECEIPT            |
+-------------------------------------------------------------+
| Student ID      : STU101                                    |
| Book Catalog ID : B101                                      |
| Prescribed Due  : 2026-11-19                                |
| Actual Return   : 2026-11-24                                |
| Overdue Days    : 5 day(s)                                  |
| Penalty Rate    : Rs. 5.00                                  |
| Total Fine Due  : Rs. 25.00                                 |
+-------------------------------------------------------------+
```

### Executive Audit Report
```
============================================================================
                 NEXLIB EXECUTIVE AUDIT & STATISTICAL REPORT
============================================================================
| Total Catalog Titles                : 6                                  |
| Books Currently Available           : 5                                  |
| Books In Circulation (Issued)       : 1                                  |
| Total Registered Students           : 3                                  |
| Active Issue Loans                  : 1                                  |
| Overdue Loans Alert                 : 0                                  |
| Active Reservation Requests         : 1                                  |
| Total Fines Assessed                : Rs. 25.00                          |
| Total Fines Collected               : Rs. 25.00                          |
| Outstanding Pending Balance         : Rs. 0.00                           |
----------------------------------------------------------------------------
```

---

## 11. Built-in Viva & Architecture Guide

The application includes a built-in Viva & Technical Architecture module accessible directly from Option 4 of the main menu, explaining:
- **OOP Concepts:** Inheritance (`User` -> `Student`, `Librarian`), Abstraction, Polymorphism (`displayProfile`, method overloads), Encapsulation.
- **Collections Framework:** `ArrayList` for dynamic records, `HashMap` for O(1) RFID tag lookup, `Queue`/`LinkedList` for FIFO reservation waitlist.
- **Exception Handling:** `LibraryException` hierarchy handling validation, duplicate, and availability errors.
- **File Persistence:** Object serialization via `DataStore` with automatic `.bak` fail-safe recovery.
- **Hardware Simulation:** `RFIDScanner` simulating contactless 13.56 MHz RFID readers.

---

## 12. Future Scope

1. **Hardware Integration:** Connect physical USB/Serial RC522 or PN532 RFID readers via `javax.comm` or JSerialComm.
2. **Database Migration:** Upgrade persistence layer to JDBC with SQLite, PostgreSQL, or MySQL.
3. **Automated Notification:** Integrate SMTP or WhatsApp Business API to dispatch automated SMS/Email alerts for approaching due dates.
4. **Barcode/QR Support:** Hybrid circulation supporting both optical QR codes and RFID chips.

---
*Developed with pure Core Java for Academic & Engineering Excellence.*
