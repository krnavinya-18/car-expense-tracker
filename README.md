# Car Expense Tracking System

---

## 1. Project Title

**Car Expense Tracking System**

---

## 2. Project Description

A professional, browser-based Car Expense Tracking System developed as a college mini-project. The application allows users to record, view, search, filter, edit, delete, and analyse all car-related expenses through a clean and modern web dashboard. All data is persisted in an Oracle Database and the browser interface is generated entirely from Java using Vaadin Flow.

---

## 3. Project Objectives

- Enable systematic recording of car expenses across multiple categories.
- Provide a real-time dashboard showing key financial statistics.
- Allow flexible searching, filtering, and sorting of expense records.
- Generate category-wise and monthly spending reports with visual charts.
- Demonstrate a complete, layered Java web application using industry-standard frameworks.

---

## 4. Technology Stack

| Layer      | Technology                          |
|------------|-------------------------------------|
| Language   | Java 21                             |
| Framework  | Spring Boot 3.3.5                   |
| UI         | Vaadin Flow 24.4.13                 |
| Database   | Oracle Database (XE / Standard)     |
| ORM        | Spring Data JPA / Hibernate         |
| JDBC       | Oracle JDBC Driver (ojdbc11)        |
| Build Tool | Apache Maven 3.9+                   |

> **Note:** The entire browser UI is generated from Java. No HTML, CSS, JavaScript, or TypeScript files are written manually.

---

## 5. System Requirements

- **Java:** JDK 21 or later
- **Maven:** Apache Maven 3.9 or later
- **Database:** Oracle Database XE 21c (or any Oracle 12c+)
- **RAM:** 512 MB minimum (2 GB recommended)
- **Disk:** 500 MB free space
- **Browser:** Any modern browser (Chrome, Firefox, Edge)

---

## 6. Project Structure

```
car-expense-tracker/
├── pom.xml                                   # Maven build file
├── README.md                                 # This file
├── .gitignore                                # Git ignore rules
└── src/
    ├── main/
    │   ├── java/com/carexpense/
    │   │   ├── CarExpenseApplication.java    # Spring Boot entry point
    │   │   ├── model/
    │   │   │   └── Expense.java              # JPA entity (CAR_EXPENSES table)
    │   │   ├── repository/
    │   │   │   └── ExpenseRepository.java    # Spring Data JPA repository
    │   │   ├── service/
    │   │   │   ├── ExpenseService.java       # Business logic for expenses
    │   │   │   └── ReportService.java        # Report aggregation logic
    │   │   ├── config/
    │   │   │   └── DatabaseConfig.java       # Oracle DataSource configuration
    │   │   └── view/
    │   │       ├── MainLayout.java           # App shell with sidebar navigation
    │   │       ├── DashboardView.java        # Page 1 — Dashboard statistics
    │   │       ├── AddExpenseView.java        # Page 2 — Add Expense form
    │   │       ├── ViewExpensesView.java      # Page 3 — View / Search / Edit / Delete
    │   │       └── ReportsView.java           # Page 4 — Spending reports and charts
    │   └── resources/
    │       ├── application.properties        # App configuration
    │       └── schema.sql                    # Oracle table creation script
    └── test/
        └── java/com/carexpense/
            └── CarExpenseApplicationTests.java
```

---

## 7. Database Setup

### Create the Oracle Schema

Connect to Oracle Database using SQL*Plus or SQL Developer and run the schema script:

```sql
-- Connect as your Oracle user
sqlplus username/password@localhost:1521/XE

-- Run the schema file
@src/main/resources/schema.sql
```

Or copy-paste the contents of `src/main/resources/schema.sql` directly into SQL Developer and execute it.

### Table: CAR_EXPENSES

| Column       | Oracle Type       | Constraint       |
|--------------|-------------------|------------------|
| EXPENSE_ID   | NUMBER(10)        | PRIMARY KEY      |
| EXPENSE_DATE | DATE              | NOT NULL         |
| CATEGORY     | VARCHAR2(100)     | NOT NULL         |
| AMOUNT       | NUMBER(12,2)      | NOT NULL         |
| DESCRIPTION  | VARCHAR2(500)     | NULLABLE         |

The primary key is auto-incremented using the `CAR_EXPENSES_SEQ` Oracle sequence and the `CAR_EXPENSES_BI` trigger.

---

## 8. Oracle Configuration

Database credentials are configured via environment variables. **Never hardcode passwords in source files.**

### Option A — Environment Variables (Recommended)

Set these before running the application:

```bash
# Windows Command Prompt
set DB_URL=jdbc:oracle:thin:@localhost:1521/XE
set DB_USERNAME=your_username
set DB_PASSWORD=your_password

# Windows PowerShell
$env:DB_URL="jdbc:oracle:thin:@localhost:1521/XE"
$env:DB_USERNAME="your_username"
$env:DB_PASSWORD="your_password"
```

### Option B — Edit application.properties

Open `src/main/resources/application.properties` and update:

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521/XE
spring.datasource.username=your_oracle_username
spring.datasource.password=your_oracle_password
```

> **Warning:** If using Option B, never commit `application.properties` containing real passwords to GitHub. Add it to `.gitignore`.

### Oracle Connection String Formats

| Setup           | JDBC URL                                      |
|-----------------|-----------------------------------------------|
| Oracle XE local | `jdbc:oracle:thin:@localhost:1521/XE`         |
| Oracle XE SID   | `jdbc:oracle:thin:@localhost:1521:XE`         |
| Remote server   | `jdbc:oracle:thin:@hostname:port/servicename` |

---

## 9. Build and Compilation

### Clone the Repository

```bash
git clone https://github.com/your-username/car-expense-tracker.git
cd car-expense-tracker
```

### Compile the Project

```bash
mvn clean compile
```

### Run All Tests

```bash
mvn test
```

### Package into JAR

```bash
mvn clean package -DskipTests
```

---

## 10. Application Execution

### Step 1 — Set database credentials (see Section 8)

### Step 2 — Start the application

```bash
# Using Maven
mvn spring-boot:run

# Using the packaged JAR
java -jar target/car-expense-tracker-1.0.0.jar
```

### Step 3 — Open in browser

Navigate to: **http://localhost:8080**

---

## 11. Application Modules

### Page 1 — Dashboard (`/`)
Displays live statistics: total spending, expense count, average expense, and the highest spending category. Shows the five most recent expenses and a monthly spending summary — all from real database records.

### Page 2 — Add Expense (`/add-expense`)
A validated form to register new expenses. Fields: date, category, amount, and description. Saves to Oracle Database with success/error notifications.

### Page 3 — View Expenses (`/view-expenses`)
A full-featured data table with:
- Search by category or description
- Filter by category and date range
- Column sorting
- Edit expenses via modal dialog
- Delete with confirmation
- Paginated display (15 records per page)

### Page 4 — Reports (`/reports`)
Visual spending analysis using Java-based Vaadin components:
- Summary cards (total, count, top category)
- Category-wise horizontal bar chart with totals
- Percentage distribution table
- Monthly spending bar chart

---

## 12. GitHub Repository Setup

### Initialise and Push

```bash
# Inside the project directory
git init
git add .
git commit -m "Initial commit: Car Expense Tracking System"

# Add your GitHub remote
git remote add origin https://github.com/your-username/car-expense-tracker.git

# Push to GitHub
git push -u origin main
```

### Protected Files (.gitignore)

The `.gitignore` file excludes:
- `target/` — compiled classes and JAR files
- `*.class` — Java bytecode
- `.idea/`, `*.iml` — IDE files
- `node_modules/` — Vaadin build artifacts
- `application-local.properties` — local credential overrides
- `*.env` — environment files

> Never commit files containing passwords, secret keys, or compiled artifacts.
