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
import java.util.stream.Collectors;

/**
 * Dashboard view — real-time statistics and summary overview.
 *
 * Designed with a solid dark navy blue theme (#0B1F4D), white cards,
 * clear typography, and responsive alignment.
 */
@PageTitle("Dashboard — Car Expense Tracker")
@Route(value = "", layout = MainLayout.class)
@RouteAlias(value = "dashboard", layout = MainLayout.class)
public class DashboardView extends VerticalLayout {

    private final ExpenseService expenseService;

    // Design constants
    private static final String NAVY         = "#0B1F4D";
    private static final String TEXT_MUTED   = "#64748B";
    private static final String CARD_BG      = "#FFFFFF";
    private static final String CARD_BORDER  = "1px solid #E2E8F0";
    private static final String CARD_SHADOW  = "0 2px 8px rgba(11, 31, 77, 0.05)";
    private static final String ICON_BG      = "#EEF2F9";

    // Formatters
    private static final NumberFormat INR_FORMAT =
            NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy");

    public DashboardView(ExpenseService expenseService) {
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
        content.add(createStatCards());
        content.add(createRecentExpensesSection());
        content.add(createMonthlySection());

        add(content);
    }

    private Div createHeader() {
        H2 title = new H2("Dashboard Overview");
        title.getStyle()
             .set("color", NAVY)
             .set("font-size", "1.5rem")
             .set("font-weight", "700")
             .set("margin", "0 0 0.25rem 0")
             .set("letter-spacing", "-0.3px");

        Span subtitle = new Span("Real-time summary of car expenditures from your database.");
        subtitle.getStyle()
                .set("color", TEXT_MUTED)
                .set("font-size", "0.88rem")
                .set("display", "block");

        Div header = new Div(title, subtitle);
        header.setWidthFull();
        header.getStyle().set("margin-bottom", "1rem");
        return header;
    }

    private Div createStatCards() {
        BigDecimal total   = expenseService.getTotalExpenses();
        long count         = expenseService.getTotalCount();
        BigDecimal average = expenseService.getAverageExpense();
        String topCategory = expenseService.getHighestSpendingCategory();

        Div card1 = buildStatCard("Total Spending", formatInr(total), VaadinIcon.WALLET);
        Div card2 = buildStatCard("Total Expenses", String.valueOf(count), VaadinIcon.RECORDS);
        Div card3 = buildStatCard("Average Expense", formatInr(average), VaadinIcon.CALC);
        Div card4 = buildStatCard("Highest Category", topCategory, VaadinIcon.TAG);

        Div grid = new Div(card1, card2, card3, card4);
        grid.setWidthFull();
        grid.getStyle()
            .set("display", "grid")
            .set("grid-template-columns", "repeat(auto-fit, minmax(230px, 1fr))")
            .set("gap", "1rem")
            .set("margin-bottom", "1rem");
        return grid;
    }

    private Div buildStatCard(String label, String value, VaadinIcon iconType) {
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

    private Div createRecentExpensesSection() {
        List<Expense> recent = expenseService.getRecentExpenses();

        Span title = new Span("Recent Five Expenses");
        title.getStyle()
             .set("font-size", "1.05rem")
             .set("font-weight", "600")
             .set("color", NAVY);

        Div header = new Div(title);
        header.getStyle()
              .set("padding", "1rem 1.25rem")
              .set("border-bottom", "1px solid #E2E8F0");

        Div table = new Div();
        table.setWidthFull();
        table.add(createTableHeader("Date", "Category", "Amount", "Description"));

        if (recent.isEmpty()) {
            Span empty = new Span("No expenses recorded yet.");
            empty.getStyle()
                 .set("color", TEXT_MUTED)
                 .set("font-style", "italic")
                 .set("padding", "1.5rem")
                 .set("display", "block")
                 .set("text-align", "center");
            table.add(empty);
        } else {
            for (int i = 0; i < recent.size(); i++) {
                Expense e = recent.get(i);
                String bg = (i % 2 == 0) ? "#FFFFFF" : "#FAFAFC";
                table.add(createTableRow(
                        e.getExpenseDate().format(DATE_FMT),
                        e.getCategory(),
                        formatInr(e.getAmount()),
                        e.getDescription() != null ? e.getDescription() : "—",
                        bg
                ));
            }
        }

        Div card = new Div(header, table);
        card.setWidthFull();
        card.getStyle()
            .set("background", CARD_BG)
            .set("border", CARD_BORDER)
            .set("border-radius", "10px")
            .set("box-shadow", CARD_SHADOW)
            .set("overflow", "hidden")
            .set("margin-bottom", "1rem");
        return card;
    }

    private Div createTableHeader(String col1, String col2, String col3, String col4) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setSpacing(false);
        row.setPadding(false);

        String[] headers = {col1, col2, col3, col4};
        String[] widths  = {"18%", "25%", "20%", "37%"};

        for (int i = 0; i < headers.length; i++) {
            Span th = new Span(headers[i]);
            th.getStyle()
              .set("padding", "0.75rem 1.25rem")
              .set("font-size", "0.75rem")
              .set("font-weight", "600")
              .set("color", TEXT_MUTED)
              .set("text-transform", "uppercase")
              .set("letter-spacing", "0.5px")
              .set("width", widths[i])
              .set("display", "inline-block");
            if (i == 2) {
                th.getStyle().set("text-align", "right");
            }
            row.add(th);
        }

        Div wrapper = new Div(row);
        wrapper.setWidthFull();
        wrapper.getStyle()
               .set("background", "#F8FAFC")
               .set("border-bottom", "1px solid #E2E8F0");
        return wrapper;
    }

    private Div createTableRow(String date, String category, String amount, String description, String bg) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setSpacing(false);
        row.setPadding(false);

        String[] data   = {date, category, amount, description};
        String[] widths = {"18%", "25%", "20%", "37%"};

        for (int i = 0; i < data.length; i++) {
            Span td = new Span(data[i]);
            td.getStyle()
              .set("padding", "0.75rem 1.25rem")
              .set("font-size", "0.86rem")
              .set("color", (i == 2) ? NAVY : "#1E293B")
              .set("font-weight", (i == 2) ? "600" : "400")
              .set("overflow", "hidden")
              .set("text-overflow", "ellipsis")
              .set("white-space", "nowrap")
              .set("width", widths[i])
              .set("display", "inline-block");
            if (i == 2) {
                td.getStyle().set("text-align", "right");
            }
            row.add(td);
        }

        Div wrapper = new Div(row);
        wrapper.setWidthFull();
        wrapper.getStyle()
               .set("background", bg)
               .set("border-bottom", "1px solid #F1F5F9");
        return wrapper;
    }

    private Div createMonthlySection() {
        List<Object[]> monthlyRaw = expenseService.getMonthlySpendingSummary();

        Span title = new Span("Monthly Spending Summary");
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

        if (monthlyRaw.isEmpty()) {
            Span empty = new Span("No monthly data recorded yet.");
            empty.getStyle().set("color", TEXT_MUTED).set("font-style", "italic");
            body.add(empty);
        } else {
            BigDecimal grandTotal = monthlyRaw.stream()
                    .map(r -> (r[2] instanceof BigDecimal)
                              ? (BigDecimal) r[2]
                              : BigDecimal.valueOf(((Number) r[2]).doubleValue()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            List<Object[]> displayRows = monthlyRaw.stream().limit(12).collect(Collectors.toList());

            for (Object[] row : displayRows) {
                int year  = ((Number) row[0]).intValue();
                int month = ((Number) row[1]).intValue();
                BigDecimal total = (row[2] instanceof BigDecimal)
                                   ? (BigDecimal) row[2]
                                   : BigDecimal.valueOf(((Number) row[2]).doubleValue());

                String monthName = java.time.Month.of(month)
                        .getDisplayName(java.time.format.TextStyle.SHORT, Locale.ENGLISH);
                String label = monthName + " " + year;

                double pct = grandTotal.compareTo(BigDecimal.ZERO) == 0
                             ? 0
                             : total.doubleValue() / grandTotal.doubleValue() * 100;

                body.add(createMonthlyBar(label, formatInr(total), pct));
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

    private Div createMonthlyBar(String label, String amount, double percentage) {
        Span labelSpan = new Span(label);
        labelSpan.getStyle()
                 .set("font-size", "0.85rem")
                 .set("font-weight", "500")
                 .set("color", "#1E293B")
                 .set("width", "110px")
                 .set("flex-shrink", "0");

        Div track = new Div();
        track.getStyle()
             .set("flex", "1")
             .set("height", "8px")
             .set("background", "#E2E8F0")
             .set("border-radius", "4px")
             .set("overflow", "hidden")
             .set("margin", "0 1rem");

        Div fill = new Div();
        fill.getStyle()
            .set("height", "100%")
            .set("width", String.format(Locale.US, "%.1f%%", Math.min(percentage, 100)))
            .set("background", NAVY)
            .set("border-radius", "4px");
        track.add(fill);

        Span amountSpan = new Span(amount);
        amountSpan.getStyle()
                  .set("font-size", "0.85rem")
                  .set("font-weight", "600")
                  .set("color", NAVY)
                  .set("width", "120px")
                  .set("text-align", "right")
                  .set("flex-shrink", "0");

        HorizontalLayout row = new HorizontalLayout(labelSpan, track, amountSpan);
        row.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        row.setWidthFull();
        row.setSpacing(false);
        row.setPadding(false);

        Div wrapper = new Div(row);
        wrapper.setWidthFull();
        wrapper.getStyle().set("margin-bottom", "0.85rem");
        return wrapper;
    }

    private String formatInr(BigDecimal amount) {
        if (amount == null) return "₹0.00";
        return INR_FORMAT.format(amount.setScale(2, RoundingMode.HALF_UP));
    }
}
