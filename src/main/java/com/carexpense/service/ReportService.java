package com.carexpense.service;

import com.carexpense.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

/**
 * Service for generating report data for the Reports page.
 *
 * <p>Aggregates raw data from the repository into structures
 * suitable for display in the Vaadin reports view, including
 * category-wise totals, monthly breakdowns, and percentage
 * distributions.</p>
 */
@Service
@Transactional(readOnly = true)
public class ReportService {

    private final ExpenseRepository expenseRepository;

    /**
     * Constructor injection of the expense repository.
     *
     * @param expenseRepository the JPA repository for expense entities
     */
    public ReportService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    // -------------------------------------------------------
    // Summary Statistics
    // -------------------------------------------------------

    /**
     * Returns the overall total spending across all categories.
     *
     * @return total amount as {@link BigDecimal}
     */
    public BigDecimal getTotalSpending() {
        BigDecimal total = expenseRepository.getTotalExpenses();
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Returns the total number of expense records.
     *
     * @return count of all expenses
     */
    public long getTotalExpenseCount() {
        return expenseRepository.count();
    }

    /**
     * Returns the name of the category with the highest total spending.
     *
     * @return category name or "N/A" when no data exists
     */
    public String getHighestSpendingCategory() {
        List<String> result = expenseRepository.findHighestSpendingCategory();
        return (result != null && !result.isEmpty()) ? result.get(0) : "N/A";
    }

    // -------------------------------------------------------
    // Category Report
    // -------------------------------------------------------

    /**
     * Builds a sorted map of category → total amount for all recorded categories.
     * Entries are sorted by total spending in descending order.
     *
     * @return LinkedHashMap of {category name → total BigDecimal}
     */
    public Map<String, BigDecimal> getCategorySpendingMap() {
        List<Object[]> rawData = expenseRepository.getCategoryWiseSpending();
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        for (Object[] row : rawData) {
            String category     = (String) row[0];
            BigDecimal total    = row[1] instanceof BigDecimal
                                  ? (BigDecimal) row[1]
                                  : BigDecimal.valueOf(((Number) row[1]).doubleValue());
            result.put(category, total.setScale(2, RoundingMode.HALF_UP));
        }
        return result;
    }

    /**
     * Calculates the percentage contribution of each category to the overall total.
     *
     * @return map of {category name → percentage (0-100 BigDecimal)}
     */
    public Map<String, BigDecimal> getCategoryPercentageMap() {
        Map<String, BigDecimal> spending = getCategorySpendingMap();
        BigDecimal total = getTotalSpending();
        Map<String, BigDecimal> percentages = new LinkedHashMap<>();

        if (total.compareTo(BigDecimal.ZERO) == 0) {
            spending.keySet().forEach(k -> percentages.put(k, BigDecimal.ZERO));
            return percentages;
        }

        for (Map.Entry<String, BigDecimal> entry : spending.entrySet()) {
            BigDecimal pct = entry.getValue()
                    .multiply(new BigDecimal("100"))
                    .divide(total, 1, RoundingMode.HALF_UP);
            percentages.put(entry.getKey(), pct);
        }
        return percentages;
    }

    // -------------------------------------------------------
    // Monthly Report
    // -------------------------------------------------------

    /**
     * Builds an ordered map of "Month Year" → total amount for the monthly report.
     * The map is ordered from most-recent to oldest.
     *
     * @return LinkedHashMap of {month label → total BigDecimal}
     */
    public Map<String, BigDecimal> getMonthlySpendingMap() {
        List<Object[]> rawData = expenseRepository.getMonthlySpendingSummary();
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        for (Object[] row : rawData) {
            int year  = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();
            BigDecimal total = row[2] instanceof BigDecimal
                               ? (BigDecimal) row[2]
                               : BigDecimal.valueOf(((Number) row[2]).doubleValue());

            String label = Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + year;
            result.put(label, total.setScale(2, RoundingMode.HALF_UP));
        }
        return result;
    }
}
