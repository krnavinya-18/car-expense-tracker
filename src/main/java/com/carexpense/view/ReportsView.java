package com.carexpense.view;

import com.carexpense.service.ExpenseService;
import com.carexpense.service.ReportService;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
 * Designed with a solid dark navy blue theme (#0B1F4D), white container cards,
 * clean percentage tables, and responsive bar charts.
 */
@PageTitle("Reports — Car Expense Tracker")
@Route(value = "reports", layout = MainLayout.class)
public class ReportsView extends VerticalLayout {

    private final ReportService  reportService;
    private final ExpenseService expenseService;

    // Design constants
    private static final String NAVY        = "#0B1F4D";
    private static final String TEXT_MUTED  = "#64748B";
    private static final String CARD_BG     = "#FFFFFF";
    private static final String CARD_BORDER = "1px solid #E2E8F0";
    private static final String CARD_SHADOW = "0 2px 8px rgba(11, 31, 77, 0.05)";
    private static final String ICON_BG     = "#EEF2F9";

    // Formatters
    private static final NumberFormat INR_FORMAT =
            NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    public ReportsView(ReportService reportService, ExpenseService expenseService) {
        this.reportService  = reportService;
        this.expenseService = expenseService;
        buildUI();
    }

    private void buildUI() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", "#F4F6FA");

        VerticalLayout content = new VerticalLayout();
        content.setWidthFull();
        content.setMaxWidth("1200px");
        content.getStyle()
               .set("margin", "0 auto")
               .set("padding", "2rem 1.5rem");
        content.setSpacing(true);

        content.add(createHeader());
        content.add(createSummaryCards());
        content.add(createTwoColumnSection());
        content.add(createMonthlySection());

        add(content);
    }

    private Div createHeader() {
        H2 title = new H2("Expense Reports");
        title.getStyle()
             .set("color", NAVY)
             .set("font-size", "1.5rem")
             .set("font-weight", "700")
             .set("margin", "0 0 0.25rem 0")
             .set("letter-spacing", "-0.3px");

        Span subtitle = new Span("Category breakdown and historical monthly spending analysis.");
        subtitle.getStyle()
                .set("color", TEXT_MUTED)
                .set("font-size", "0.88rem")
                .set("display", "block");

        Div header = new Div(title, subtitle);
        header.setWidthFull();
        header.getStyle().set("margin-bottom", "1rem");
        return header;
    }

    private Div createSummaryCards() {
        BigDecimal total   = reportService.getTotalSpending();
        long       count   = reportService.getTotalExpenseCount();
        String     topCat  = reportService.getHighestSpendingCategory();

        Div totalCard  = buildSummaryCard("Total Spending",   formatInr(total),       VaadinIcon.WALLET);
        Div countCard  = buildSummaryCard("Total Expenses",   String.valueOf(count),   VaadinIcon.RECORDS);
        Div topCatCard = buildSummaryCard("Top Category",     topCat,                 VaadinIcon.TAG);

        Div grid = new Div(totalCard, countCard, topCatCard);
        grid.setWidthFull();
        grid.getStyle()
            .set("display", "grid")
            .set("grid-template-columns", "repeat(auto-fit, minmax(240px, 1fr))")
            .set("gap", "1rem")
            .set("margin-bottom", "1rem");
        return grid;
    }

    private Div buildSummaryCard(String label, String value, VaadinIcon iconType) {
        Icon icon = iconType.create();
        icon.setSize("1.4rem");
        icon.getStyle().set("color", NAVY);

        Div iconBox = new Div(icon);
        iconBox.getStyle()
               .set("background", ICON_BG)
               .set("border-radius", "8px")
               .set("width", "46px")
               .set("height", "46px")
               .set("display", "flex")
               .set("align-items", "center")
               .set("justify-content", "center")
               .set("flex-shrink", "0");

        Span labelSpan = new Span(label);
        labelSpan.getStyle()
                 .set("font-size", "0.75rem")
                 .set("font-weight", "600")
                 .set("color", TEXT_MUTED)
                 .set("text-transform", "uppercase")
                 .set("letter-spacing", "0.5px")
                 .set("display", "block")
                 .set("margin-bottom", "0.25rem");

        Span valueSpan = new Span(value);
        valueSpan.getStyle()
                 .set("font-size", "1.35rem")
                 .set("font-weight", "700")
                 .set("color", NAVY)
                 .set("line-height", "1.2")
                 .set("word-break", "break-word")
                 .set("display", "block");

        VerticalLayout textContent = new VerticalLayout(labelSpan, valueSpan);
        textContent.setPadding(false);
        textContent.setSpacing(false);

        HorizontalLayout row = new HorizontalLayout(iconBox, textContent);
        row.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        row.setSpacing(true);
        row.setPadding(false);
        row.setWidthFull();

        Div card = new Div(row);
        card.getStyle()
            .set("background", CARD_BG)
            .set("border", CARD_BORDER)
            .set("border-radius", "10px")
            .set("padding", "1.25rem")
            .set("box-shadow", CARD_SHADOW)
            .set("border-left", "4px solid " + NAVY);
        return card;
    }

    private Div createTwoColumnSection() {
        Div categoryChart   = createCategoryBarChart();
        Div percentageTable = createPercentageTable();

        Div container = new Div(categoryChart, percentageTable);
        container.setWidthFull();
        container.getStyle()
                 .set("display", "grid")
                 .set("grid-template-columns", "repeat(auto-fit, minmax(360px, 1fr))")
                 .set("gap", "1rem")
                 .set("margin-bottom", "1rem");
        return container;
    }

    private Div createCategoryBarChart() {
        Map<String, BigDecimal> categoryData = reportService.getCategorySpendingMap();

        Span title = new Span("Category-wise Spending");
        title.getStyle()
             .set("font-size", "1.05rem")
             .set("font-weight", "600")
             .set("color", NAVY);

        Div header = new Div(title);
        header.getStyle()
              .set("padding", "1rem 1.25rem")
              .set("border-bottom", "1px solid #E2E8F0");

        Div body = new Div();
        body.getStyle().set("padding", "1.25rem");
        body.setWidthFull();

        if (categoryData.isEmpty()) {
            body.add(emptyMessage("No expense data available."));
        } else {
            BigDecimal maxValue = categoryData.values().stream()
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ONE);

            for (Map.Entry<String, BigDecimal> entry : categoryData.entrySet()) {
                double pct = entry.getValue().doubleValue() / maxValue.doubleValue() * 100;
                body.add(createCategoryBar(entry.getKey(), formatInr(entry.getValue()), pct));
            }
        }

        Div card = new Div(header, body);
        card.getStyle()
            .set("background", CARD_BG)
            .set("border", CARD_BORDER)
            .set("border-radius", "10px")
            .set("box-shadow", CARD_SHADOW)
            .set("overflow", "hidden");
        return card;
    }

    private Div createCategoryBar(String category, String amount, double widthPct) {
        Span catLabel = new Span(category);
        catLabel.getStyle()
                .set("font-size", "0.85rem")
                .set("font-weight", "500")
                .set("color", "#1E293B")
                .set("width", "160px")
                .set("flex-shrink", "0")
                .set("overflow", "hidden")
                .set("text-overflow", "ellipsis")
                .set("white-space", "nowrap");

        Div track = new Div();
        track.getStyle()
             .set("flex", "1")
             .set("height", "8px")
             .set("background", "#E2E8F0")
             .set("border-radius", "4px")
             .set("overflow", "hidden")
             .set("margin", "0 0.75rem");

        Div fill = new Div();
        fill.getStyle()
            .set("height", "100%")
            .set("width", String.format(Locale.US, "%.1f%%", Math.min(widthPct, 100)))
            .set("background", NAVY)
            .set("border-radius", "4px");
        track.add(fill);

        Span amtLabel = new Span(amount);
        amtLabel.getStyle()
                .set("font-size", "0.85rem")
                .set("font-weight", "600")
                .set("color", NAVY)
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

    private Div createPercentageTable() {
        Map<String, BigDecimal> spending    = reportService.getCategorySpendingMap();
        Map<String, BigDecimal> percentages = reportService.getCategoryPercentageMap();
        BigDecimal total = reportService.getTotalSpending();

        Span tableTitle = new Span("Percentage Distribution");
        tableTitle.getStyle()
                  .set("font-size", "1.05rem")
                  .set("font-weight", "600")
                  .set("color", NAVY);

        Div header = new Div(tableTitle);
        header.getStyle()
              .set("padding", "1rem 1.25rem")
              .set("border-bottom", "1px solid #E2E8F0");

        Div body = new Div();
        body.setWidthFull();

        if (spending.isEmpty()) {
            Div empty = new Div(emptyMessage("No data available."));
            empty.getStyle().set("padding", "1.25rem");
            body.add(empty);
        } else {
            body.add(createPctRow(true, "Category", "Amount", "Share", null));

            boolean odd = false;
            for (Map.Entry<String, BigDecimal> entry : spending.entrySet()) {
                BigDecimal pct = percentages.getOrDefault(entry.getKey(), BigDecimal.ZERO);
                String bg = odd ? "#FAFAFC" : "#FFFFFF";
                body.add(createPctRow(false,
                        entry.getKey(),
                        formatInr(entry.getValue()),
                        pct.toPlainString() + "%",
                        bg));
                odd = !odd;
            }

            Div totalRow = createPctRow(false, "TOTAL", formatInr(total), "100%", "#F8FAFC");
            totalRow.getStyle().set("font-weight", "700").set("border-top", "2px solid #E2E8F0");
            body.add(totalRow);
        }

        Div card = new Div(header, body);
        card.getStyle()
            .set("background", CARD_BG)
            .set("border", CARD_BORDER)
            .set("border-radius", "10px")
            .set("box-shadow", CARD_SHADOW)
            .set("overflow", "hidden");
        return card;
    }

    private Div createPctRow(boolean isHeader, String category, String amount, String pct, String bg) {
        String[] vals   = {category, amount, pct};
        String[] widths = {"52%", "30%", "18%"};

        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setPadding(false);
        row.setSpacing(false);

        for (int i = 0; i < vals.length; i++) {
            Span cell = new Span(vals[i]);
            cell.getStyle()
                .set("padding", isHeader ? "0.75rem 1rem" : "0.65rem 1rem")
                .set("font-size", isHeader ? "0.75rem" : "0.85rem")
                .set("font-weight", isHeader ? "600" : (i == 1 ? "600" : "400"))
                .set("color", isHeader ? TEXT_MUTED : (i == 1 ? NAVY : "#1E293B"))
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
            wrapper.getStyle().set("background", "#F8FAFC").set("border-bottom", "1px solid #E2E8F0");
        } else {
            wrapper.getStyle().set("border-bottom", "1px solid #F1F5F9");
        }
        return wrapper;
    }

    private Div createMonthlySection() {
        Map<String, BigDecimal> monthlyData = reportService.getMonthlySpendingMap();

        Span title = new Span("Monthly Spending Breakdown");
        title.getStyle()
             .set("font-size", "1.05rem")
             .set("font-weight", "600")
             .set("color", NAVY);

        Div header = new Div(title);
        header.getStyle()
              .set("padding", "1rem 1.25rem")
              .set("border-bottom", "1px solid #E2E8F0");

        Div body = new Div();
        body.getStyle()
            .set("padding", "1.25rem")
            .set("display", "grid")
            .set("grid-template-columns", "repeat(auto-fit, minmax(360px, 1fr))")
            .set("gap", "0.5rem 1.5rem");

        if (monthlyData.isEmpty()) {
            body.getStyle().set("display", "block");
            body.add(emptyMessage("No monthly data recorded yet."));
        } else {
            BigDecimal maxVal = monthlyData.values().stream()
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ONE);

            for (Map.Entry<String, BigDecimal> entry : monthlyData.entrySet()) {
                double pct = entry.getValue().doubleValue() / maxVal.doubleValue() * 100;
                body.add(createMonthlyBar(entry.getKey(), formatInr(entry.getValue()), pct));
            }
        }

        Div card = new Div(header, body);
        card.setWidthFull();
        card.getStyle()
            .set("background", CARD_BG)
            .set("border", CARD_BORDER)
            .set("border-radius", "10px")
            .set("box-shadow", CARD_SHADOW)
            .set("overflow", "hidden");
        return card;
    }

    private Div createMonthlyBar(String label, String amount, double widthPct) {
        Span monthLabel = new Span(label);
        monthLabel.getStyle()
                  .set("font-size", "0.85rem")
                  .set("font-weight", "500")
                  .set("color", "#1E293B")
                  .set("width", "90px")
                  .set("flex-shrink", "0");

        Div track = new Div();
        track.getStyle()
             .set("flex", "1")
             .set("height", "8px")
             .set("background", "#E2E8F0")
             .set("border-radius", "4px")
             .set("overflow", "hidden")
             .set("margin", "0 0.75rem");

        Div fill = new Div();
        fill.getStyle()
            .set("height", "100%")
            .set("width", String.format(Locale.US, "%.1f%%", Math.min(widthPct, 100)))
            .set("background", NAVY)
            .set("border-radius", "4px");
        track.add(fill);

        Span amtLabel = new Span(amount);
        amtLabel.getStyle()
                .set("font-size", "0.85rem")
                .set("font-weight", "600")
                .set("color", NAVY)
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
        wrapper.getStyle().set("margin-bottom", "0.75rem");
        return wrapper;
    }

    private String formatInr(BigDecimal amount) {
        if (amount == null) return "₹0.00";
        return INR_FORMAT.format(amount.setScale(2, RoundingMode.HALF_UP));
    }

    private Span emptyMessage(String message) {
        Span span = new Span(message);
        span.getStyle()
            .set("color", TEXT_MUTED)
            .set("font-style", "italic")
            .set("font-size", "0.88rem");
        return span;
    }
}
