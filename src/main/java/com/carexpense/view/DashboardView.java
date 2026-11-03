package com.carexpense.view;

import com.carexpense.model.Expense;
import com.carexpense.service.ExpenseService;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Dashboard view — the application's home page.
 *
 * <p>Displays live summary statistics (total spending, expense count, average,
 * highest category) sourced from the Oracle database, a table of the five most
 * recent expenses, and a monthly spending summary.</p>
 */
@PageTitle("Dashboard — Car Expense Tracker")
@Route(value = "", layout = MainLayout.class)
@RouteAlias(value = "dashboard", layout = MainLayout.class)
public class DashboardView extends VerticalLayout {

    private final ExpenseService expenseService;

    // Indian Rupee formatter
    private static final NumberFormat INR_FORMAT =
            NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy");

    /**
     * Constructs the dashboard view with all statistic cards and tables.
     *
     * @param expenseService the service for fetching expense statistics
     */
    public DashboardView(ExpenseService expenseService) {
        this.expenseService = expenseService;
        buildUI();
    }

    // -------------------------------------------------------
    // UI Construction
    // -------------------------------------------------------

    /**
     * Assembles the full dashboard layout.
     */
    private void buildUI() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", "#f4f6fb");

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setPadding(true);
        content.setSpacing(true);
        content.getStyle()
               .set("max-width", "1300px")
               .set("margin", "0 auto")
               .set("padding", "1.5rem");

        content.add(createPageHeader());
        content.add(createStatCards());
        content.add(createRecentExpensesSection());
        content.add(createMonthlySection());

        add(content);
    }

    /**
     * Creates the page title header.
     */
    private Div createPageHeader() {
        H2 title = new H2("Dashboard Overview");
        title.getStyle()
             .set("color", "#1a237e")
             .set("font-size", "1.6rem")
             .set("font-weight", "700")
             .set("margin", "0 0 0.25rem 0");

        Span subtitle = new Span("Real-time summary of your car expenses");
        subtitle.getStyle()
                .set("color", "#546e7a")
                .set("font-size", "0.9rem");

        Div header = new Div(title, subtitle);
        header.getStyle().set("margin-bottom", "1rem");
        return header;
    }

    // -------------------------------------------------------
    // Stat Cards Row
    // -------------------------------------------------------

    /**
     * Creates the four summary statistic cards using live database values.
     */
    private HorizontalLayout createStatCards() {
        BigDecimal total   = expenseService.getTotalExpenses();
        long count         = expenseService.getTotalCount();
        BigDecimal average = expenseService.getAverageExpense();
        String topCategory = expenseService.getHighestSpendingCategory();

        Div totalCard   = buildStatCard("Total Spending",   formatInr(total),             VaadinIcon.MONEY, "#1565c0", "#e3f2fd");
        Div countCard   = buildStatCard("Total Expenses",   String.valueOf(count),         VaadinIcon.LIST,  "#2e7d32", "#e8f5e9");
        Div avgCard     = buildStatCard("Average Expense",  formatInr(average),            VaadinIcon.CHART, "#e65100", "#fff3e0");
        Div topCatCard  = buildStatCard("Top Category",     topCategory,                   VaadinIcon.STAR,  "#6a1b9a", "#f3e5f5");

        HorizontalLayout cards = new HorizontalLayout(totalCard, countCard, avgCard, topCatCard);
        cards.setWidthFull();
        cards.setSpacing(true);
        cards.getStyle().set("flex-wrap", "wrap");
        cards.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.STRETCH);
        return cards;
    }

    /**
     * Builds a single summary statistic card.
     */
    private Div buildStatCard(String label, String value, VaadinIcon iconType,
                               String accentColor, String bgColor) {
        Icon icon = iconType.create();
        icon.setSize("2rem");
        icon.getStyle().set("color", accentColor);

        Div iconBox = new Div(icon);
        iconBox.getStyle()
               .set("background", bgColor)
               .set("border-radius", "50%")
               .set("width", "56px")
               .set("height", "56px")
               .set("display", "flex")
               .set("align-items", "center")
               .set("justify-content", "center")
               .set("flex-shrink", "0");

        Span valueSpan = new Span(value);
        valueSpan.getStyle()
                 .set("font-size", "1.45rem")
                 .set("font-weight", "700")
                 .set("color", "#1a237e")
                 .set("word-break", "break-word");

        Span labelSpan = new Span(label);
        labelSpan.getStyle()
                 .set("font-size", "0.8rem")
                 .set("color", "#78909c")
                 .set("font-weight", "500")
                 .set("text-transform", "uppercase")
                 .set("letter-spacing", "0.5px");

        VerticalLayout textContent = new VerticalLayout(valueSpan, labelSpan);
        textContent.setPadding(false);
        textContent.setSpacing(false);

        HorizontalLayout cardContent = new HorizontalLayout(iconBox, textContent);
        cardContent.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        cardContent.setSpacing(true);

        Div card = new Div(cardContent);
        card.getStyle()
            .set("background", "#ffffff")
            .set("border-radius", "12px")
            .set("padding", "1.25rem")
            .set("box-shadow", "0 2px 12px rgba(0,0,0,0.08)")
            .set("flex", "1 1 220px")
            .set("min-width", "200px")
            .set("border-left", "4px solid " + accentColor)
            .set("transition", "box-shadow 0.2s ease");
        return card;
    }

    // -------------------------------------------------------
    // Recent Expenses Table
    // -------------------------------------------------------

    /**
     * Creates the "Recent Expenses" section with the last five records.
     */
    private Div createRecentExpensesSection() {
        List<Expense> recent = expenseService.getRecentExpenses();

        Span sectionTitle = new Span("Recent Expenses");
        sectionTitle.getStyle()
                    .set("font-size", "1.1rem")
                    .set("font-weight", "600")
                    .set("color", "#1a237e");

        Div headerRow = new Div(sectionTitle);
        headerRow.getStyle()
                 .set("padding", "1rem 1.25rem 0.75rem")
                 .set("border-bottom", "1px solid #e8eaf6");

        Div table = new Div();
        table.setWidthFull();

        // Table header
        table.add(createTableRow(true, "Date", "Category", "Amount", "Description"));

        if (recent.isEmpty()) {
            Span empty = new Span("No expenses recorded yet. Add your first expense.");
            empty.getStyle()
                 .set("color", "#90a4ae")
                 .set("font-style", "italic")
                 .set("padding", "1.5rem")
                 .set("display", "block")
                 .set("text-align", "center");
            table.add(empty);
        } else {
            for (int i = 0; i < recent.size(); i++) {
                Expense e = recent.get(i);
                String bg = (i % 2 == 0) ? "#fafafa" : "#ffffff";
                table.add(createExpenseRow(
                        e.getExpenseDate().format(DATE_FMT),
                        e.getCategory(),
                        formatInr(e.getAmount()),
                        e.getDescription() != null ? e.getDescription() : "—",
                        bg
                ));
            }
        }

        Div card = new Div(headerRow, table);
        card.setWidthFull();
        card.getStyle()
            .set("background", "#ffffff")
            .set("border-radius", "12px")
            .set("box-shadow", "0 2px 12px rgba(0,0,0,0.08)")
            .set("overflow", "hidden")
            .set("margin-top", "0.5rem");
        return card;
    }

    /**
     * Creates a table header or data row.
     */
    private Div createTableRow(boolean isHeader, String... cols) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setSpacing(false);
        row.setPadding(false);

        String[] widths = {"15%", "25%", "18%", "42%"};
        for (int i = 0; i < cols.length; i++) {
            Span cell = new Span(cols[i]);
            cell.getStyle()
                .set("padding", isHeader ? "0.7rem 1rem" : "0.65rem 1rem")
                .set("font-size", isHeader ? "0.78rem" : "0.88rem")
                .set("font-weight", isHeader ? "700" : "400")
                .set("color", isHeader ? "#546e7a" : "#37474f")
                .set("text-transform", isHeader ? "uppercase" : "none")
                .set("letter-spacing", isHeader ? "0.5px" : "0")
                .set("overflow", "hidden")
                .set("text-overflow", "ellipsis")
                .set("white-space", "nowrap")
                .set("width", widths[i])
                .set("display", "inline-block");
            if (i == 2) { // Amount — right-align
                cell.getStyle()
                    .set("text-align", "right")
                    .set("font-weight", isHeader ? "700" : "600")
                    .set("color", isHeader ? "#546e7a" : "#1565c0");
            }
            row.add(cell);
        }

        Div rowDiv = new Div(row);
        rowDiv.setWidthFull();
        if (isHeader) {
            rowDiv.getStyle()
                  .set("background", "#f8f9fd")
                  .set("border-bottom", "1px solid #e8eaf6");
        }
        return rowDiv;
    }

    /**
     * Creates a single data row for the recent expenses table.
     */
    private Div createExpenseRow(String date, String category, String amount,
                                  String description, String bg) {
        Div row = createTableRow(false, date, category, amount, description);
        row.getStyle().set("background", bg);
        return row;
    }

    // -------------------------------------------------------
    // Monthly Summary Section
    // -------------------------------------------------------

    /**
     * Creates the monthly spending summary section.
     */
    private Div createMonthlySection() {
        List<Object[]> monthlyRaw = expenseService.getMonthlySpendingSummary();

        Span sectionTitle = new Span("Monthly Spending Summary");
        sectionTitle.getStyle()
                    .set("font-size", "1.1rem")
                    .set("font-weight", "600")
                    .set("color", "#1a237e");

        Div headerRow = new Div(sectionTitle);
        headerRow.getStyle()
                 .set("padding", "1rem 1.25rem 0.75rem")
                 .set("border-bottom", "1px solid #e8eaf6");

        Div body = new Div();
        body.getStyle().set("padding", "1rem 1.25rem");
        body.setWidthFull();

        if (monthlyRaw.isEmpty()) {
            Span empty = new Span("No monthly data available yet.");
            empty.getStyle()
                 .set("color", "#90a4ae")
                 .set("font-style", "italic");
            body.add(empty);
        } else {
            // Calculate grand total for percentage bars
            BigDecimal grandTotal = monthlyRaw.stream()
                    .map(r -> (r[2] instanceof BigDecimal)
                              ? (BigDecimal) r[2]
                              : BigDecimal.valueOf(((Number) r[2]).doubleValue()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Show up to 12 months
            List<Object[]> displayRows = monthlyRaw.stream().limit(12).collect(Collectors.toList());

            for (Object[] row : displayRows) {
                int year  = ((Number) row[0]).intValue();
                int month = ((Number) row[1]).intValue();
                BigDecimal total = (row[2] instanceof BigDecimal)
                                   ? (BigDecimal) row[2]
                                   : BigDecimal.valueOf(((Number) row[2]).doubleValue());

                String monthName = java.time.Month.of(month)
                        .getDisplayName(java.time.format.TextStyle.FULL, Locale.ENGLISH);
                String label = monthName + " " + year;

                double pct = grandTotal.compareTo(BigDecimal.ZERO) == 0
                             ? 0
                             : total.doubleValue() / grandTotal.doubleValue() * 100;

                body.add(createMonthlyBar(label, formatInr(total), pct));
            }
        }

        Div card = new Div(headerRow, body);
        card.setWidthFull();
        card.getStyle()
            .set("background", "#ffffff")
            .set("border-radius", "12px")
            .set("box-shadow", "0 2px 12px rgba(0,0,0,0.08)")
            .set("overflow", "hidden")
            .set("margin-top", "0.5rem");
        return card;
    }

    /**
     * Creates a single horizontal bar representing one month's spending.
     */
    private Div createMonthlyBar(String label, String amount, double percentage) {
        Span labelSpan = new Span(label);
        labelSpan.getStyle()
                 .set("font-size", "0.88rem")
                 .set("color", "#37474f")
                 .set("width", "140px")
                 .set("flex-shrink", "0");

        // Bar track
        Div barTrack = new Div();
        barTrack.getStyle()
                .set("flex", "1")
                .set("height", "10px")
                .set("background", "#e8eaf6")
                .set("border-radius", "6px")
                .set("overflow", "hidden")
                .set("margin", "0 1rem");

        // Filled portion
        Div barFill = new Div();
        barFill.getStyle()
               .set("height", "100%")
               .set("width", String.format("%.1f%%", Math.min(percentage, 100)))
               .set("background", "linear-gradient(90deg, #1565c0, #42a5f5)")
               .set("border-radius", "6px");
        barTrack.add(barFill);

        Span amountSpan = new Span(amount);
        amountSpan.getStyle()
                  .set("font-size", "0.88rem")
                  .set("font-weight", "600")
                  .set("color", "#1565c0")
                  .set("width", "120px")
                  .set("text-align", "right")
                  .set("flex-shrink", "0");

        HorizontalLayout row = new HorizontalLayout(labelSpan, barTrack, amountSpan);
        row.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        row.setWidthFull();
        row.setSpacing(false);
        row.setPadding(false);

        Div wrapper = new Div(row);
        wrapper.setWidthFull();
        wrapper.getStyle().set("margin-bottom", "0.75rem");
        return wrapper;
    }

    // -------------------------------------------------------
    // Utility
    // -------------------------------------------------------

    /**
     * Formats a {@link BigDecimal} as an Indian Rupee currency string.
     *
     * @param amount the amount to format
     * @return formatted string such as "₹1,234.50"
     */
    private String formatInr(BigDecimal amount) {
        if (amount == null) return "₹0.00";
        return INR_FORMAT.format(amount.setScale(2, RoundingMode.HALF_UP));
    }
}
