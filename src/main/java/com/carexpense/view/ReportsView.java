package com.carexpense.view;

import com.carexpense.service.ExpenseService;
import com.carexpense.service.ReportService;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * Reports view — visual spending analysis page.
 *
 * <p>Displays:
 * <ul>
 *   <li>Overall summary statistics (total, count, highest category)</li>
 *   <li>Category-wise spending breakdown with visual bar charts and percentages</li>
 *   <li>Monthly spending breakdown with visual bar charts</li>
 * </ul>
 * All data is sourced live from the Oracle database. Charts are rendered
 * using Java-based Vaadin components (no external JavaScript charting libraries).</p>
 */
@PageTitle("Reports — Car Expense Tracker")
@Route(value = "reports", layout = MainLayout.class)
public class ReportsView extends VerticalLayout {

    private final ReportService  reportService;
    private final ExpenseService expenseService;

    private static final NumberFormat INR_FORMAT =
            NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    /**
     * Constructs the Reports view.
     *
     * @param reportService  the service for aggregated reporting data
     * @param expenseService the service for raw expense statistics
     */
    public ReportsView(ReportService reportService, ExpenseService expenseService) {
        this.reportService  = reportService;
        this.expenseService = expenseService;
        buildUI();
    }

    // -------------------------------------------------------
    // UI Construction
    // -------------------------------------------------------

    /**
     * Assembles the complete reports layout.
     */
    private void buildUI() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", "#f4f6fb");

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.getStyle()
               .set("max-width", "1300px")
               .set("margin", "0 auto")
               .set("padding", "1.5rem");
        content.setSpacing(true);

        content.add(createPageHeader());
        content.add(createSummaryCards());
        content.add(createTwoColumnSection());
        content.add(createMonthlySection());

        add(content);
    }

    /**
     * Creates the page title header.
     */
    private Div createPageHeader() {
        H2 title = new H2("Expense Reports");
        title.getStyle()
             .set("color", "#1a237e")
             .set("font-size", "1.6rem")
             .set("font-weight", "700")
             .set("margin", "0 0 0.25rem 0");

        Span subtitle = new Span("Detailed analysis and breakdown of your car expenses.");
        subtitle.getStyle().set("color", "#546e7a").set("font-size", "0.9rem");

        Div header = new Div(title, subtitle);
        header.getStyle().set("margin-bottom", "0.5rem");
        return header;
    }

    // -------------------------------------------------------
    // Summary Cards
    // -------------------------------------------------------

    /**
     * Creates the three top-level summary statistic cards.
     */
    private HorizontalLayout createSummaryCards() {
        BigDecimal total   = reportService.getTotalSpending();
        long       count   = reportService.getTotalExpenseCount();
        String     topCat  = reportService.getHighestSpendingCategory();

        Div totalCard  = buildSummaryCard("Total Spending",    formatInr(total), VaadinIcon.MONEY,   "#1565c0", "#e3f2fd");
        Div countCard  = buildSummaryCard("Total Expenses",    String.valueOf(count), VaadinIcon.LIST, "#2e7d32", "#e8f5e9");
        Div topCatCard = buildSummaryCard("Highest Category",  topCat, VaadinIcon.STAR,              "#6a1b9a", "#f3e5f5");

        HorizontalLayout row = new HorizontalLayout(totalCard, countCard, topCatCard);
        row.setWidthFull();
        row.setSpacing(true);
        row.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.STRETCH);
        return row;
    }

    /**
     * Builds a single summary card.
     */
    private Div buildSummaryCard(String label, String value, VaadinIcon iconType,
                                  String accentColor, String bgColor) {
        var icon = iconType.create();
        icon.setSize("1.8rem");
        icon.getStyle().set("color", accentColor);

        Div iconBox = new Div(icon);
        iconBox.getStyle()
               .set("background", bgColor)
               .set("border-radius", "50%")
               .set("width", "52px").set("height", "52px")
               .set("display", "flex")
               .set("align-items", "center")
               .set("justify-content", "center")
               .set("flex-shrink", "0");

        Span valueSpan = new Span(value);
        valueSpan.getStyle()
                 .set("font-size", "1.4rem")
                 .set("font-weight", "700")
                 .set("color", "#1a237e")
                 .set("word-break", "break-word");

        Span labelSpan = new Span(label);
        labelSpan.getStyle()
                 .set("font-size", "0.78rem")
                 .set("color", "#78909c")
                 .set("text-transform", "uppercase")
                 .set("letter-spacing", "0.5px");

        VerticalLayout text = new VerticalLayout(valueSpan, labelSpan);
        text.setPadding(false);
        text.setSpacing(false);

        HorizontalLayout cardContent = new HorizontalLayout(iconBox, text);
        cardContent.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        cardContent.setSpacing(true);

        Div card = new Div(cardContent);
        card.getStyle()
            .set("background", "#ffffff")
            .set("border-radius", "12px")
            .set("padding", "1.25rem")
            .set("box-shadow", "0 2px 12px rgba(0,0,0,0.08)")
            .set("flex", "1")
            .set("border-left", "4px solid " + accentColor);
        return card;
    }

    // -------------------------------------------------------
    // Two-Column Section: Category + Percentage Table
    // -------------------------------------------------------

    /**
     * Creates a two-column layout with category bar chart (left) and
     * percentage distribution table (right).
     */
    private HorizontalLayout createTwoColumnSection() {
        Div categoryChart = createCategoryBarChart();
        Div percentageTable = createPercentageTable();

        HorizontalLayout row = new HorizontalLayout(categoryChart, percentageTable);
        row.setWidthFull();
        row.setSpacing(true);
        row.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.STRETCH);
        return row;
    }

    /**
     * Creates the category-wise spending bar chart card.
     */
    private Div createCategoryBarChart() {
        Map<String, BigDecimal> categoryData = reportService.getCategorySpendingMap();

        Span title = new Span("Category-wise Spending");
        title.getStyle()
             .set("font-size", "1.05rem")
             .set("font-weight", "600")
             .set("color", "#1a237e");

        Div headerRow = new Div(title);
        headerRow.getStyle()
                 .set("padding", "1rem 1.25rem 0.75rem")
                 .set("border-bottom", "1px solid #e8eaf6");

        Div body = new Div();
        body.getStyle().set("padding", "1rem 1.25rem");
        body.setWidthFull();

        if (categoryData.isEmpty()) {
            body.add(emptyMessage("No expense data available."));
        } else {
            BigDecimal maxValue = categoryData.values().stream()
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ONE);

            // Color palette for bars
            String[] colors = {
                "#1565c0", "#2e7d32", "#e65100", "#6a1b9a",
                "#00838f", "#c62828", "#f57f17", "#37474f", "#558b2f"
            };
            int colorIdx = 0;

            for (Map.Entry<String, BigDecimal> entry : categoryData.entrySet()) {
                double pct = entry.getValue().doubleValue() / maxValue.doubleValue() * 100;
                String color = colors[colorIdx % colors.length];
                body.add(createCategoryBar(entry.getKey(), formatInr(entry.getValue()), pct, color));
                colorIdx++;
            }
        }

        Div card = new Div(headerRow, body);
        card.getStyle()
            .set("background", "#ffffff")
            .set("border-radius", "12px")
            .set("box-shadow", "0 2px 12px rgba(0,0,0,0.08)")
            .set("overflow", "hidden")
            .set("flex", "1.5");
        return card;
    }

    /**
     * Creates a single horizontal bar entry for a category.
     */
    private Div createCategoryBar(String category, String amount, double widthPct, String color) {
        Span catLabel = new Span(category);
        catLabel.getStyle()
                .set("font-size", "0.85rem")
                .set("color", "#37474f")
                .set("width", "170px")
                .set("flex-shrink", "0")
                .set("overflow", "hidden")
                .set("text-overflow", "ellipsis")
                .set("white-space", "nowrap");

        // Bar track
        Div track = new Div();
        track.getStyle()
             .set("flex", "1")
             .set("height", "12px")
             .set("background", "#e8eaf6")
             .set("border-radius", "6px")
             .set("overflow", "hidden")
             .set("margin", "0 0.75rem");

        Div fill = new Div();
        fill.getStyle()
            .set("height", "100%")
            .set("width", String.format("%.1f%%", Math.min(widthPct, 100)))
            .set("background", color)
            .set("border-radius", "6px");
        track.add(fill);

        Span amtLabel = new Span(amount);
        amtLabel.getStyle()
                .set("font-size", "0.85rem")
                .set("font-weight", "600")
                .set("color", color)
                .set("width", "110px")
                .set("text-align", "right")
                .set("flex-shrink", "0");

        HorizontalLayout row = new HorizontalLayout(catLabel, track, amtLabel);
        row.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        row.setWidthFull();
        row.setPadding(false);
        row.setSpacing(false);

        Div wrapper = new Div(row);
        wrapper.setWidthFull();
        wrapper.getStyle().set("margin-bottom", "0.85rem");
        return wrapper;
    }

    /**
     * Creates the percentage distribution table card.
     */
    private Div createPercentageTable() {
        Map<String, BigDecimal> spending    = reportService.getCategorySpendingMap();
        Map<String, BigDecimal> percentages = reportService.getCategoryPercentageMap();
        BigDecimal total = reportService.getTotalSpending();

        Span tableTitle = new Span("Percentage Distribution");
        tableTitle.getStyle()
                  .set("font-size", "1.05rem")
                  .set("font-weight", "600")
                  .set("color", "#1a237e");

        Div headerRow = new Div(tableTitle);
        headerRow.getStyle()
                 .set("padding", "1rem 1.25rem 0.75rem")
                 .set("border-bottom", "1px solid #e8eaf6");

        Div body = new Div();
        body.getStyle().set("padding", "0.5rem 0");

        if (spending.isEmpty()) {
            Div empty = new Div(emptyMessage("No data available."));
            empty.getStyle().set("padding", "1rem 1.25rem");
            body.add(empty);
        } else {
            // Table header
            body.add(createPctTableRow(true, "Category", "Amount", "%", null));

            boolean odd = false;
            for (Map.Entry<String, BigDecimal> entry : spending.entrySet()) {
                BigDecimal pct = percentages.getOrDefault(entry.getKey(), BigDecimal.ZERO);
                String bg = odd ? "#fafafa" : "#ffffff";
                body.add(createPctTableRow(false,
                        entry.getKey(),
                        formatInr(entry.getValue()),
                        pct.toPlainString() + "%",
                        bg));
                odd = !odd;
            }

            // Total row
            Div totalRow = createPctTableRow(false, "TOTAL", formatInr(total), "100%", "#e8eaf6");
            totalRow.getStyle().set("font-weight", "700").set("border-top", "2px solid #c5cae9");
            body.add(totalRow);
        }

        Div card = new Div(headerRow, body);
        card.getStyle()
            .set("background", "#ffffff")
            .set("border-radius", "12px")
            .set("box-shadow", "0 2px 12px rgba(0,0,0,0.08)")
            .set("overflow", "hidden")
            .set("flex", "1");
        return card;
    }

    /**
     * Creates a row in the percentage distribution table.
     */
    private Div createPctTableRow(boolean isHeader, String category,
                                   String amount, String pct, String bg) {
        String[] vals   = {category, amount, pct};
        String[] widths = {"55%", "30%", "15%"};

        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setPadding(false);
        row.setSpacing(false);

        for (int i = 0; i < vals.length; i++) {
            Span cell = new Span(vals[i]);
            cell.getStyle()
                .set("padding", isHeader ? "0.6rem 1rem" : "0.55rem 1rem")
                .set("font-size", isHeader ? "0.74rem" : "0.84rem")
                .set("font-weight", isHeader ? "700" : "400")
                .set("color", isHeader ? "#546e7a" : "#37474f")
                .set("text-transform", isHeader ? "uppercase" : "none")
                .set("letter-spacing", isHeader ? "0.5px" : "0")
                .set("width", widths[i])
                .set("display", "inline-block")
                .set("overflow", "hidden")
                .set("text-overflow", "ellipsis")
                .set("white-space", "nowrap");
            if (i > 0) {
                cell.getStyle().set("text-align", "right");
            }
            row.add(cell);
        }

        Div wrapper = new Div(row);
        wrapper.setWidthFull();
        if (bg != null) {
            wrapper.getStyle().set("background", bg);
        }
        if (isHeader) {
            wrapper.getStyle().set("background", "#f8f9fd");
        }
        return wrapper;
    }

    // -------------------------------------------------------
    // Monthly Section
    // -------------------------------------------------------

    /**
     * Creates the monthly spending bar chart card.
     */
    private Div createMonthlySection() {
        Map<String, BigDecimal> monthlyData = reportService.getMonthlySpendingMap();

        Span title = new Span("Monthly Spending Breakdown");
        title.getStyle()
             .set("font-size", "1.05rem")
             .set("font-weight", "600")
             .set("color", "#1a237e");

        Div headerRow = new Div(title);
        headerRow.getStyle()
                 .set("padding", "1rem 1.25rem 0.75rem")
                 .set("border-bottom", "1px solid #e8eaf6");

        Div body = new Div();
        body.getStyle()
            .set("padding", "1rem 1.25rem")
            .set("display", "grid")
            .set("grid-template-columns", "repeat(auto-fill, minmax(420px, 1fr))")
            .set("gap", "0.5rem 2rem");

        if (monthlyData.isEmpty()) {
            body.getStyle().set("display", "block");
            body.add(emptyMessage("No monthly data available."));
        } else {
            BigDecimal maxVal = monthlyData.values().stream()
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ONE);

            for (Map.Entry<String, BigDecimal> entry : monthlyData.entrySet()) {
                double pct = entry.getValue().doubleValue() / maxVal.doubleValue() * 100;
                body.add(createMonthlyBar(entry.getKey(), formatInr(entry.getValue()), pct));
            }
        }

        Div card = new Div(headerRow, body);
        card.setWidthFull();
        card.getStyle()
            .set("background", "#ffffff")
            .set("border-radius", "12px")
            .set("box-shadow", "0 2px 12px rgba(0,0,0,0.08)")
            .set("overflow", "hidden");
        return card;
    }

    /**
     * Creates a single horizontal bar row for the monthly chart.
     */
    private Div createMonthlyBar(String label, String amount, double widthPct) {
        Span monthLabel = new Span(label);
        monthLabel.getStyle()
                  .set("font-size", "0.85rem")
                  .set("color", "#37474f")
                  .set("width", "90px")
                  .set("flex-shrink", "0");

        Div track = new Div();
        track.getStyle()
             .set("flex", "1")
             .set("height", "12px")
             .set("background", "#e8eaf6")
             .set("border-radius", "6px")
             .set("overflow", "hidden")
             .set("margin", "0 0.75rem");

        Div fill = new Div();
        fill.getStyle()
            .set("height", "100%")
            .set("width", String.format("%.1f%%", Math.min(widthPct, 100)))
            .set("background", "linear-gradient(90deg, #1565c0, #42a5f5)")
            .set("border-radius", "6px");
        track.add(fill);

        Span amtLabel = new Span(amount);
        amtLabel.getStyle()
                .set("font-size", "0.85rem")
                .set("font-weight", "600")
                .set("color", "#1565c0")
                .set("width", "110px")
                .set("text-align", "right")
                .set("flex-shrink", "0");

        HorizontalLayout row = new HorizontalLayout(monthLabel, track, amtLabel);
        row.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        row.setWidthFull();
        row.setPadding(false);
        row.setSpacing(false);

        Div wrapper = new Div(row);
        wrapper.setWidthFull();
        wrapper.getStyle().set("margin-bottom", "0.6rem");
        return wrapper;
    }

    // -------------------------------------------------------
    // Utility
    // -------------------------------------------------------

    /**
     * Formats a BigDecimal as an Indian Rupee currency string.
     */
    private String formatInr(BigDecimal amount) {
        if (amount == null) return "₹0.00";
        return INR_FORMAT.format(amount.setScale(2, RoundingMode.HALF_UP));
    }

    /**
     * Creates a styled empty-state message component.
     */
    private Span emptyMessage(String message) {
        Span span = new Span(message);
        span.getStyle()
            .set("color", "#90a4ae")
            .set("font-style", "italic")
            .set("font-size", "0.9rem");
        return span;
    }
}
