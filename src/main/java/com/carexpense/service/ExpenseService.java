package com.carexpense.service;

import com.carexpense.model.Expense;
import com.carexpense.repository.ExpenseRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for all expense management operations.
 *
 * <p>Encapsulates business logic for creating, reading, updating,
 * and deleting expense records. All database interactions go through
 * this service to maintain a clean layered architecture.</p>
 */
@Service
@Transactional
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    /**
     * Constructor injection of the expense repository.
     *
     * @param expenseRepository the JPA repository for expense entities
     */
    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    // -------------------------------------------------------
    // CRUD Operations
    // -------------------------------------------------------

    /**
     * Save a new expense or update an existing one.
     *
     * @param expense the expense to persist
     * @return the saved expense with generated ID
     * @throws IllegalArgumentException if the expense or its required fields are null/invalid
     */
    public Expense saveExpense(Expense expense) {
        validateExpense(expense);
        return expenseRepository.save(expense);
    }

    /**
     * Retrieve all expense records ordered by date descending.
     *
     * @return list of all expenses
     */
    @Transactional(readOnly = true)
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll(
                org.springframework.data.domain.Sort.by(
                        org.springframework.data.domain.Sort.Direction.DESC, "expenseDate", "id"));
    }

    /**
     * Find a single expense by its ID.
     *
     * @param id the expense ID
     * @return an Optional containing the expense if found
     */
    @Transactional(readOnly = true)
    public Optional<Expense> getExpenseById(Long id) {
        return expenseRepository.findById(id);
    }

    /**
     * Delete an expense by its ID.
     *
     * @param id the ID of the expense to delete
     * @throws jakarta.persistence.EntityNotFoundException if no expense exists with this ID
     */
    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new jakarta.persistence.EntityNotFoundException("Expense not found with ID: " + id);
        }
        expenseRepository.deleteById(id);
    }

    // -------------------------------------------------------
    // Search and Filter Operations
    // -------------------------------------------------------

    /**
     * Search expenses by category and/or a keyword in description or category name.
     *
     * @param category optional category filter (null to skip)
     * @param searchTerm optional search keyword (null to skip)
     * @return filtered list of expenses
     */
    @Transactional(readOnly = true)
    public List<Expense> searchExpenses(String category, String searchTerm) {
        boolean hasCategory  = category != null && !category.isBlank() && !category.equals("All Categories");
        boolean hasSearchTerm = searchTerm != null && !searchTerm.isBlank();

        if (hasCategory && hasSearchTerm) {
            return expenseRepository.findByCategoryAndSearchTerm(category, searchTerm);
        } else if (hasCategory) {
            return expenseRepository.findByCategoryIgnoreCase(category);
        } else if (hasSearchTerm) {
            return expenseRepository.searchByCategoryOrDescription(searchTerm);
        } else {
            return getAllExpenses();
        }
    }

    /**
     * Find all expenses within a given date range.
     *
     * @param startDate start of the date range (inclusive)
     * @param endDate   end of the date range (inclusive)
     * @return list of expenses within the range
     */
    @Transactional(readOnly = true)
    public List<Expense> getExpensesByDateRange(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByExpenseDateBetween(startDate, endDate);
    }

    // -------------------------------------------------------
    // Statistics / Aggregations
    // -------------------------------------------------------

    /**
     * Calculate the total amount of all recorded expenses.
     *
     * @return total expenditure as {@link BigDecimal}
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalExpenses() {
        BigDecimal total = expenseRepository.getTotalExpenses();
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Calculate the average expense amount across all records.
     *
     * @return average expense amount
     */
    @Transactional(readOnly = true)
    public BigDecimal getAverageExpense() {
        BigDecimal avg = expenseRepository.getAverageExpense();
        return avg != null ? avg : BigDecimal.ZERO;
    }

    /**
     * Get the total count of expense records.
     *
     * @return number of expense records
     */
    @Transactional(readOnly = true)
    public long getTotalCount() {
        return expenseRepository.count();
    }

    /**
     * Find the category with the highest cumulative spending.
     *
     * @return the name of the top spending category, or "N/A" if no data exists
     */
    @Transactional(readOnly = true)
    public String getHighestSpendingCategory() {
        List<String> categories = expenseRepository.findHighestSpendingCategory();
        return (categories != null && !categories.isEmpty()) ? categories.get(0) : "N/A";
    }

    /**
     * Get the five most recent expenses for the dashboard.
     *
     * @return list of up to five recent expenses
     */
    @Transactional(readOnly = true)
    public List<Expense> getRecentExpenses() {
        return expenseRepository.findTop5RecentExpenses(PageRequest.of(0, 5));
    }

    /**
     * Get spending totals grouped by category.
     *
     * @return list of [category (String), total (BigDecimal)] arrays
     */
    @Transactional(readOnly = true)
    public List<Object[]> getCategoryWiseSpending() {
        return expenseRepository.getCategoryWiseSpending();
    }

    /**
     * Get monthly spending summary grouped by year and month.
     *
     * @return list of [year (Number), month (Number), total (BigDecimal)] arrays
     */
    @Transactional(readOnly = true)
    public List<Object[]> getMonthlySpendingSummary() {
        return expenseRepository.getMonthlySpendingSummary();
    }

    // -------------------------------------------------------
    // Helper / Validation
    // -------------------------------------------------------

    /**
     * Returns all supported expense categories.
     *
     * @return ordered list of expense category names
     */
    public static List<String> getAllCategories() {
        return List.of(
                "Fuel",
                "Maintenance & Service",
                "Repairs & Parts",
                "Insurance",
                "Parking",
                "Tolls",
                "Car Wash",
                "Registration & Taxes",
                "Other"
        );
    }

    /**
     * Validates an expense before saving.
     *
     * @param expense the expense to validate
     * @throws IllegalArgumentException for any validation failure
     */
    private void validateExpense(Expense expense) {
        if (expense == null) {
            throw new IllegalArgumentException("Expense cannot be null.");
        }
        if (expense.getExpenseDate() == null) {
            throw new IllegalArgumentException("Expense date is required.");
        }
        if (expense.getCategory() == null || expense.getCategory().isBlank()) {
            throw new IllegalArgumentException("Expense category is required.");
        }
        if (expense.getAmount() == null) {
            throw new IllegalArgumentException("Expense amount is required.");
        }
        if (expense.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Expense amount must be greater than zero.");
        }
    }
}
