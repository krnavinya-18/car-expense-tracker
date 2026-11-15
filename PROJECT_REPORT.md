# KONGU ENGINEERING COLLEGE (AUTONOMOUS)
### DEPARTMENT OF INFORMATION TECHNOLOGY
### 24ITT32 - PROGRAMMING IN JAVA

---

# INDIVIDUAL PROJECT ON
## CAR EXPENSE TRACKING SYSTEM
### BROWSER-BASED AUTOMOTIVE EXPENDITURE MONITORING PLATFORM

---

## PROJECT REPORT
**Submitted by:**  
**NAVINYA K R**  
**Roll No: 25ITR098**  

**KONGU ENGINEERING COLLEGE (AUTONOMOUS)**  
**PERUNDURAI, ERODE — 638 060**  

---

## 1. AIM
Development of **Car Expense Tracking System**, a comprehensive Java-based browser application for monitoring, categorizing, and analyzing automobile running expenditures using Java 21, Spring Boot, Vaadin Flow, Oracle Database, Spring Data JPA, and Apache Maven.

---

## 2. OBJECTIVE
To design and implement a modern, responsive, and robust enterprise web system that enables vehicle owners and fleet managers to systematically record vehicle operational costs across various categories (Fuel, Maintenance, Repairs, Insurance, Parking, Tolls, Car Wash, and Taxes). The system provides a live financial dashboard with instant key performance metrics, a full-featured management grid with multi-parameter search, category and date filtering, inline modal editing, secure deletion, and visual cost breakdown charts with percentage distributions. All frontend components and backend services are written purely in Java using Vaadin Flow, backed by Oracle Database persistence and safe ORM transactions.

---

## 3. FLOW CHART

```
                        +---------------------------------------------+
                        |                    START                    |
                        |   Launch CarExpenseApplication.java         |
                        +---------------------------------------------+
                                               |
                                               v
                        +---------------------------------------------+
                        |                 INITIALIZE                  |
                        |   Embedded Tomcat starts on port 8080       |
                        |   DataSource establishes Database connection|
                        +---------------------------------------------+
                                               |
                                               v
                        +---------------------------------------------+
                        |                   REQUEST                   |
                        |   Browser navigates to localhost:8080/      |
                        +---------------------------------------------+
                                               |
                                               v
                        +---------------------------------------------+
                        |             PROCESS & ROUTING               |
                        |   Vaadin maps request to View component     |
                        +---------------------------------------------+
                                               |
                                               v
                        +---------------------------------------------+
                        |           SERVICE & REPOSITORY              |
                        |   ExpenseService calls ExpenseRepository    |
                        +---------------------------------------------+
                                               |
                                               v
                        +---------------------------------------------+
                        |              DATABASE QUERY                 |
                        |   Oracle DB / H2 executes JPQL aggregates   |
                        +---------------------------------------------+
                                               |
                                               v
                        +---------------------------------------------+
                        |                RENDER VIEW                  |
                        |   Vaadin generates Java UI to browser DOM   |
                        +---------------------------------------------+
                                               |
                                               v
                        +---------------------------------------------+
                        |                USER ACTION                  |
                        |   Add / Search / Filter / Edit / Delete     |
                        +---------------------------------------------+
                                               |
                                               v
                        +---------------------------------------------+
                        |             VALIDATE & PERSIST              |
                        |   Perform validation & commit to Oracle DB  |
                        +---------------------------------------------+
                                               |
                                               v
                        +---------------------------------------------+
                        |                  RESPONSE                   |
                        |   Show notification, refresh UI & tables    |
                        +---------------------------------------------+
                                               |
                                               v
                        +---------------------------------------------+
                        |                    END                      |
                        |   Continue session or close application     |
                        +---------------------------------------------+
```

---

## 4. PROCEDURE
- ❖ Create a Maven-based Spring Boot project using Java 21, and configure dependencies in `pom.xml` for `spring-boot-starter-data-jpa`, `vaadin-spring-boot-starter` (v24.4.13), and Oracle JDBC driver (`ojdbc11`), along with H2 in-memory database for local zero-configuration development.
- ❖ Configure `application.properties` to manage database connection parameters using environment variables (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`), setting Hibernate dialect to OracleDialect and enabling `schema.sql` execution on application startup.
- ❖ Design and write `schema.sql` to create the Oracle table `CAR_EXPENSES`, an auto-incrementing sequence `CAR_EXPENSES_SEQ`, a pre-insert trigger `CAR_EXPENSES_BI`, and performance indexes on expense date and category columns.
- ❖ Create the JPA entity class `Expense.java` in the `com.carexpense.model` package, mapping database columns to Java fields (`id`, `expenseDate`, `category`, `amount`, `description`) with appropriate JPA annotations (`@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@SequenceGenerator`, `@Column`).
- ❖ Develop `ExpenseRepository.java` extending `JpaRepository<Expense, Long>` to define standard CRUD operations and custom JPQL aggregate queries for total spending, average expense, count, recent 5 expenses, category-wise grouping, and monthly spending extraction.
- ❖ Implement `ExpenseService.java` and `ReportService.java` to encapsulate core business rules, validation logic (ensuring non-null dates, valid categories, and positive currency amounts), search and date-range filtering, and mathematical percentage calculations.
- ❖ Construct the UI navigation shell in `MainLayout.java` extending Vaadin's `AppLayout`, incorporating a solid Dark Navy Blue (`#0B1F4D`) top navigation header, top-aligned sidebar navigation drawer, brand identity, and custom CSS rules for active item highlight (`#193A70` with white left border indicator).
- ❖ Develop `DashboardView.java` to present 4 responsive metric cards (Total Spending, Total Expenses, Average Expense, Top Category), the Recent Five Expenses data table, and monthly spending horizontal bar indicators.
- ❖ Build `AddExpenseView.java` featuring a centered registration card with DatePicker, ComboBox, BigDecimalField, and TextArea, complete with input validation and instant Vaadin success/error notifications.
- ❖ Construct `ViewExpensesView.java` with an interactive search bar, category dropdown filter, from/to date pickers, sortable grid columns, modal edit dialog, and delete confirmation dialog; and implement `ReportsView.java` with category bar charts, percentage tables, and monthly expenditure trends.
- ❖ Create `ContactView.java` displaying academic project details, direct communication channels, and an interactive feedback form; execute unit tests with JUnit 5; and synchronize all commits to the GitHub repository.

---

## 5. SYSTEM ARCHITECTURE
The Car Expense Tracking System follows a clean, layered enterprise architecture: **Model – Repository – Service – View**. A client browser request triggers Vaadin Flow’s server-driven engine. The active View invokes the Service layer to execute business rules and query validations. The Service layer communicates with the Spring Data JPA Repository, which translates operations into safe, parameterized SQL executed against the Oracle Database. Results are bound directly to Java component models, and Vaadin automatically synchronizes the client DOM without manual JavaScript or HTML generation.

### APPLICATION COMPONENTS:
| Layer | Files / Components | Responsibility |
|---|---|---|
| **Entry Point** | `CarExpenseApplication.java` | Bootstrap class annotated with `@SpringBootApplication`; starts embedded Tomcat server. |
| **Layout & Shell** | `MainLayout.java` | Application shell providing fixed dark navy sidebar (`#0B1F4D`), top navbar, and navigation routing. |
| **View Layer (UI)** | `DashboardView.java`<br>`AddExpenseView.java`<br>`ViewExpensesView.java`<br>`ReportsView.java`<br>`ContactView.java` | Renders interactive browser screens: live dashboard metrics, expense entry form, management grid with search/filter/edit/delete, visual spending reports, and contact form. |
| **Service Layer** | `ExpenseService.java`<br>`ReportService.java` | Encapsulates business logic, input validation, transaction handling, search/filter algorithms, and percentage calculations. |
| **Repository Layer** | `ExpenseRepository.java` | Spring Data JPA interface executing CRUD methods and custom JPQL aggregate queries. |
| **Model Layer** | `Expense.java` | JPA entity representing the `CAR_EXPENSES` relational table with primary key sequence generation. |
| **Configuration** | `DatabaseConfig.java` | Configures Oracle `DataSource` bean dynamically using environment variable overrides. |

---

## 6. DATA STORAGE
Data persistence is governed by **Oracle Database** (with H2 in-memory database supported via the `dev` profile). The primary table `CAR_EXPENSES` stores all transaction records. Primary keys are auto-generated via the `CAR_EXPENSES_SEQ` sequence and the `CAR_EXPENSES_BI` trigger.

### Oracle Table Schema (schema.sql):
```sql
CREATE SEQUENCE CAR_EXPENSES_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE TABLE CAR_EXPENSES (
    EXPENSE_ID   NUMBER(10)     NOT NULL,
    EXPENSE_DATE DATE           NOT NULL,
    CATEGORY     VARCHAR2(100)  NOT NULL,
    AMOUNT       NUMBER(12, 2)  NOT NULL,
    DESCRIPTION  VARCHAR2(500),
    CONSTRAINT PK_CAR_EXPENSES PRIMARY KEY (EXPENSE_ID)
);

CREATE OR REPLACE TRIGGER CAR_EXPENSES_BI
    BEFORE INSERT ON CAR_EXPENSES FOR EACH ROW
BEGIN
    IF :NEW.EXPENSE_ID IS NULL THEN
        SELECT CAR_EXPENSES_SEQ.NEXTVAL INTO :NEW.EXPENSE_ID FROM DUAL;
    END IF;
END;
/

CREATE INDEX IDX_CAR_EXP_DATE     ON CAR_EXPENSES (EXPENSE_DATE);
CREATE INDEX IDX_CAR_EXP_CATEGORY ON CAR_EXPENSES (CATEGORY);
```

---

## 7. RESULT
Thus, the **Car Expense Tracking System** was successfully designed, developed, and deployed using Java 21, Spring Boot, Vaadin Flow, Oracle Database, Spring Data JPA, and Apache Maven. The application provides comprehensive car expense registration, real-time dashboard analytics, multi-criteria search and filtering, inline modal editing, secure deletion, and visual cost breakdown charts with percentage distributions. It demonstrates clean Model-View-Service-Repository architecture, safe ORM persistence, enterprise UI styling using a Dark Navy Blue (`#0B1F4D`) theme, and unit testing. The project meets all academic requirements and is version-controlled on GitHub.

---

## 8. REFERENCES
1. Spring Boot Reference Documentation: https://docs.spring.io/spring-boot/
2. Vaadin Flow Reference Documentation: https://vaadin.com/docs/latest/flow
3. Spring Data JPA Reference Guide: https://docs.spring.io/spring-data/jpa/reference/
4. Oracle Database JDBC Developer's Guide: https://docs.oracle.com/en/database/oracle/oracle-database/21/jjdbc/
5. Hibernate ORM Documentation: https://hibernate.org/orm/documentation/6.5/
6. Oracle Java 21 Platform Specification: https://docs.oracle.com/en/java/javase/21/
7. Apache Maven Project Documentation: https://maven.apache.org/guides/
8. JUnit 5 User Guide: https://junit.org/junit5/docs/current/user-guide/
9. H2 Database Engine Documentation: https://www.h2database.com/html/main.html
10. Jakarta Persistence API (JPA) Specification: https://jakarta.ee/specifications/persistence/

---

## 9. GITHUB REPOSITORY LINK

| Item | Details |
|---|---|
| **LINK:** | **https://github.com/krnavinya-18/car-expense-tracker** |

---
**— END OF REPORT —**
