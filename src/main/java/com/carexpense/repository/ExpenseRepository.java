package com.carexpense.repository;

import com.carexpense.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data JPA repository for {@link Expense} entities.
 *
 * <p>Provides standard CRUD operations plus custom query methods
 * for searching, filtering, and reporting on expense data stored
 * in the Oracle CAR_EXPENSES table.</p>
 */
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // -------------------------------------------------------
    // Search and Filter Queries
    // -------------------------------------------------------

    /**
     * Find all expenses for a specific category (case-insensitive).
     */
    List<Expense> findByCategoryIgnoreCase(String category);

    /**
     * Find expenses with description containing the given keyword (case-insensitive).
     */
    List<Expense> findByDescriptionContainingIgnoreCase(String keyword);

    /**
     * Find expenses whose category or description contains the search term.
     */
    @Query("SELECT e FROM Expense e WHERE " +
           "LOWER(e.category) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<Expense> searchByCategoryOrDescription(@Param("term") String term);

    /**
     * Find all expenses within a date range (inclusive).
     */
    List<Expense> findByExpenseDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find expenses by category within a date range.
     */
    @Query("SELECT e FROM Expense e WHERE e.category = :category " +
           "AND e.expenseDate BETWEEN :start AND :end")
    List<Expense> findByCategoryAndDateRange(
            @Param("category") String category,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    /**
     * Find expenses by category and/or search term.
     */
    @Query("SELECT e FROM Expense e WHERE " +
           "(:category IS NULL OR e.category = :category) AND " +
           "(:term IS NULL OR LOWER(e.category) LIKE LOWER(CONCAT('%', :term, '%')) " +
           "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :term, '%')))")
    List<Expense> findByCategoryAndSearchTerm(
            @Param("category") String category,
            @Param("term") String term);

    // -------------------------------------------------------
    // Aggregate / Statistics Queries
    // -------------------------------------------------------

    /**
     * Calculate the total amount of all expenses.
     */
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e")
    BigDecimal getTotalExpenses();

    /**
     * Calculate the average expense amount.
     */
    @Query("SELECT COALESCE(AVG(e.amount), 0) FROM Expense e")
    BigDecimal getAverageExpense();

    /**
     * Count total number of expense records.
     */
    @Query("SELECT COUNT(e) FROM Expense e")
    Long getTotalCount();

    /**
     * Find the category with the highest total spending.
     */
    @Query("SELECT e.category FROM Expense e GROUP BY e.category " +
           "ORDER BY SUM(e.amount) DESC")
    List<String> findHighestSpendingCategory();

    // -------------------------------------------------------
    // Dashboard / Report Queries
    // -------------------------------------------------------

    /**
     * Get the five most recently added expense records.
     */
    @Query("SELECT e FROM Expense e ORDER BY e.expenseDate DESC, e.id DESC")
    List<Expense> findTop5RecentExpenses(org.springframework.data.domain.Pageable pageable);

    /**
     * Get total spending grouped by category.
     */
    @Query("SELECT e.category, SUM(e.amount) FROM Expense e GROUP BY e.category ORDER BY SUM(e.amount) DESC")
    List<Object[]> getCategoryWiseSpending();

    /**
     * Get monthly spending summary: year, month, and total amount.
     */
    @Query("SELECT EXTRACT(YEAR FROM e.expenseDate), EXTRACT(MONTH FROM e.expenseDate), SUM(e.amount) " +
           "FROM Expense e GROUP BY EXTRACT(YEAR FROM e.expenseDate), EXTRACT(MONTH FROM e.expenseDate) " +
           "ORDER BY EXTRACT(YEAR FROM e.expenseDate) DESC, EXTRACT(MONTH FROM e.expenseDate) DESC")
    List<Object[]> getMonthlySpendingSummary();

    /**
     * Get all distinct categories that have been used.
     */
    @Query("SELECT DISTINCT e.category FROM Expense e ORDER BY e.category")
    List<String> findDistinctCategories();
}
