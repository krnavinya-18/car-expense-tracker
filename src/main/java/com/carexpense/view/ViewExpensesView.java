package com.carexpense.view;

import com.carexpense.model.Expense;
import com.carexpense.service.ExpenseService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * View Expenses page — searchable, filterable, sortable, and editable expense grid.
 *
 * Designed with a solid dark navy blue theme (#0B1F4D), white container cards,
 * responsive filtering, and polished modal controls.
 */
@PageTitle("View Expenses — Car Expense Tracker")
@Route(value = "view-expenses", layout = MainLayout.class)
public class ViewExpensesView extends VerticalLayout {

    private final ExpenseService expenseService;

    // Design constants
    private static final String NAVY        = "#0B1F4D";
    private static final String TEXT_MUTED  = "#64748B";
    private static final String CARD_BG     = "#FFFFFF";
    private static final String CARD_BORDER = "1px solid #E2E8F0";
    private static final String CARD_SHADOW = "0 2px 8px rgba(11, 31, 77, 0.05)";

    // Grid and data provider
    private Grid<Expense>             expenseGrid;
    private ListDataProvider<Expense> dataProvider;

    // Filter controls
    private TextField        searchField;
    private ComboBox<String> categoryFilter;
    private DatePicker       dateFrom;
    private DatePicker       dateTo;

    // Formatters
    private static final NumberFormat    INR_FORMAT = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
    private static final DateTimeFormatter DATE_FMT  = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public ViewExpensesView(ExpenseService expenseService) {
        this.expenseService = expenseService;
        buildUI();
    }

    private void buildUI() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", "#F4F6FA");

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setMaxWidth("1200px");
        content.getStyle()
               .set("margin", "0 auto")
               .set("padding", "2rem 1.5rem");
        content.setSpacing(true);

        content.add(createHeader());
        content.add(createFiltersCard());
        content.add(createGridCard());

        add(content);
    }

    private Div createHeader() {
        H2 title = new H2("All Expenses");
        title.getStyle()
             .set("color", NAVY)
             .set("font-size", "1.5rem")
             .set("font-weight", "700")
             .set("margin", "0 0 0.25rem 0")
             .set("letter-spacing", "-0.3px");

        Span subtitle = new Span("Search, filter, edit, or delete car expense records from your database.");
        subtitle.getStyle()
                .set("color", TEXT_MUTED)
                .set("font-size", "0.88rem")
                .set("display", "block");

        Div header = new Div(title, subtitle);
        header.setWidthFull();
        header.getStyle().set("margin-bottom", "1rem");
        return header;
    }

    private Div createFiltersCard() {
        searchField = new TextField("Search");
        searchField.setPlaceholder("Category or description...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);
        searchField.setWidth("280px");
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> applyFilters());

        categoryFilter = new ComboBox<>("Category");
        categoryFilter.setItems(ExpenseService.getAllCategories());
        categoryFilter.setPlaceholder("All Categories");
        categoryFilter.setClearButtonVisible(true);
        categoryFilter.setWidth("200px");
        categoryFilter.addValueChangeListener(e -> applyFilters());

        dateFrom = new DatePicker("From Date");
        dateFrom.setWidth("150px");
        dateFrom.setClearButtonVisible(true);
        dateFrom.addValueChangeListener(e -> applyFilters());

        dateTo = new DatePicker("To Date");
        dateTo.setWidth("150px");
        dateTo.setClearButtonVisible(true);
        dateTo.addValueChangeListener(e -> applyFilters());

        Button clearFilterBtn = new Button("Clear Filters", VaadinIcon.CLOSE.create());
        clearFilterBtn.getStyle()
                      .set("background", "transparent")
                      .set("color", NAVY)
                      .set("border", "1px solid #CBD5E1")
                      .set("font-weight", "500")
                      .set("font-size", "0.85rem")
                      .set("border-radius", "6px")
                      .set("padding", "0.55rem 1rem")
                      .set("cursor", "pointer")
                      .set("align-self", "flex-end");
        clearFilterBtn.addClickListener(e -> clearFilters());

        HorizontalLayout filters = new HorizontalLayout(
                searchField, categoryFilter, dateFrom, dateTo, clearFilterBtn);
        filters.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        filters.getStyle().set("flex-wrap", "wrap").set("gap", "0.75rem");
        filters.setSpacing(false);
        filters.setWidthFull();

        Div card = new Div(filters);
        card.setWidthFull();
        card.getStyle()
            .set("background", CARD_BG)
            .set("border", CARD_BORDER)
            .set("border-radius", "10px")
            .set("padding", "1.25rem")
            .set("box-shadow", CARD_SHADOW)
            .set("margin-bottom", "1rem");
        return card;
    }

    private Div createGridCard() {
        expenseGrid = buildGrid();

        Div card = new Div(expenseGrid);
        card.setWidthFull();
        card.setHeight("480px");
        card.getStyle()
            .set("background", CARD_BG)
            .set("border", CARD_BORDER)
            .set("border-radius", "10px")
            .set("box-shadow", CARD_SHADOW)
            .set("overflow", "hidden");
        return card;
    }

    private Grid<Expense> buildGrid() {
        List<Expense> allExpenses = expenseService.getAllExpenses();
        dataProvider = new ListDataProvider<>(allExpenses);

        Grid<Expense> grid = new Grid<>(Expense.class, false);
        grid.setDataProvider(dataProvider);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS);
        grid.setSizeFull();
        grid.setPageSize(15);

        // ID column
        grid.addColumn(Expense::getId)
            .setHeader("ID")
            .setWidth("75px")
            .setFlexGrow(0)
            .setSortable(true)
            .setKey("id");

        // Date column
        grid.addColumn(e -> e.getExpenseDate().format(DATE_FMT))
            .setHeader("Date")
            .setWidth("130px")
            .setFlexGrow(0)
            .setSortable(true)
            .setKey("expenseDate")
            .setComparator((a, b) -> a.getExpenseDate().compareTo(b.getExpenseDate()));

        // Category column
        grid.addColumn(Expense::getCategory)
            .setHeader("Category")
            .setFlexGrow(1)
            .setSortable(true)
            .setKey("category");

        // Amount column
        grid.addColumn(e -> formatInr(e.getAmount()))
            .setHeader("Amount")
            .setWidth("135px")
            .setFlexGrow(0)
            .setSortable(true)
            .setComparator((a, b) -> a.getAmount().compareTo(b.getAmount()))
            .setKey("amount");

        // Description column
        grid.addColumn(e -> e.getDescription() != null ? e.getDescription() : "—")
            .setHeader("Description")
            .setFlexGrow(2)
            .setKey("description");

        // Actions column
        grid.addColumn(new ComponentRenderer<>(this::buildActionsCell))
            .setHeader("Actions")
            .setWidth("130px")
            .setFlexGrow(0)
            .setKey("actions");

        grid.getColumns().forEach(col -> col.setResizable(true));
        return grid;
    }

    private HorizontalLayout buildActionsCell(Expense expense) {
        Button editBtn = new Button(VaadinIcon.EDIT.create());
        editBtn.getStyle()
               .set("background", "transparent")
               .set("color", NAVY)
               .set("border", "none")
               .set("cursor", "pointer")
               .set("padding", "0.25rem");
        editBtn.setTooltipText("Edit this expense");
        editBtn.addClickListener(e -> openEditDialog(expense));

        Button deleteBtn = new Button(VaadinIcon.TRASH.create());
        deleteBtn.getStyle()
                 .set("background", "transparent")
                 .set("color", "#991B1B")
                 .set("border", "none")
                 .set("cursor", "pointer")
                 .set("padding", "0.25rem");
        deleteBtn.setTooltipText("Delete this expense");
        deleteBtn.addClickListener(e -> confirmDelete(expense));

        HorizontalLayout actions = new HorizontalLayout(editBtn, deleteBtn);
        actions.setSpacing(true);
        actions.setPadding(false);
        return actions;
    }

    private void applyFilters() {
        dataProvider.setFilter(expense -> {
            String search   = searchField.getValue().trim().toLowerCase();
            String category = categoryFilter.getValue();
            LocalDate from  = dateFrom.getValue();
            LocalDate to    = dateTo.getValue();

            if (!search.isEmpty()) {
                boolean matchCat  = expense.getCategory().toLowerCase().contains(search);
                boolean matchDesc = expense.getDescription() != null
                                    && expense.getDescription().toLowerCase().contains(search);
                if (!matchCat && !matchDesc) return false;
            }

            if (category != null && !category.isBlank()) {
                if (!expense.getCategory().equalsIgnoreCase(category)) return false;
            }

            if (from != null && expense.getExpenseDate().isBefore(from)) return false;
            if (to   != null && expense.getExpenseDate().isAfter(to))   return false;

            return true;
        });
    }

    private void clearFilters() {
        searchField.clear();
        categoryFilter.clear();
        dateFrom.clear();
        dateTo.clear();
        dataProvider.clearFilters();
    }

    private void openEditDialog(Expense expense) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Edit Expense (ID: " + expense.getId() + ")");
        dialog.setWidth("480px");
        dialog.setModal(true);
        dialog.setDraggable(true);

        DatePicker       editDate     = new DatePicker("Expense Date");
        ComboBox<String> editCategory = new ComboBox<>("Category");
        BigDecimalField  editAmount   = new BigDecimalField("Amount (₹)");
        TextArea         editDesc     = new TextArea("Description");

        editDate.setValue(expense.getExpenseDate());
        editDate.setMax(LocalDate.now());
        editDate.setWidthFull();

        editCategory.setItems(ExpenseService.getAllCategories());
        editCategory.setValue(expense.getCategory());
        editCategory.setWidthFull();

        editAmount.setValue(expense.getAmount());
        editAmount.setPrefixComponent(new Span("₹"));
        editAmount.setWidthFull();

        editDesc.setValue(expense.getDescription() != null ? expense.getDescription() : "");
        editDesc.setMaxLength(500);
        editDesc.setHeight("90px");
        editDesc.setWidthFull();

        VerticalLayout dialogContent = new VerticalLayout(editDate, editCategory, editAmount, editDesc);
        dialogContent.setPadding(false);
        dialogContent.setSpacing(true);
        dialog.add(dialogContent);

        Button saveBtn = new Button("Save Changes", VaadinIcon.CHECK.create());
        saveBtn.getStyle()
               .set("background", NAVY)
               .set("color", "#FFFFFF")
               .set("font-weight", "600")
               .set("font-size", "0.85rem")
               .set("border-radius", "6px")
               .set("padding", "0.55rem 1.25rem")
               .set("border", "none")
               .set("cursor", "pointer");
        saveBtn.addClickListener(e -> {
            if (editDate.isEmpty() || editCategory.isEmpty() || editAmount.isEmpty()) {
                showNotification("Date, Category, and Amount are required.", NotificationVariant.LUMO_ERROR);
                return;
            }
            if (editAmount.getValue().compareTo(BigDecimal.ZERO) <= 0) {
                showNotification("Amount must be greater than ₹0.", NotificationVariant.LUMO_ERROR);
                return;
            }
            try {
                expense.setExpenseDate(editDate.getValue());
                expense.setCategory(editCategory.getValue());
                expense.setAmount(editAmount.getValue());
                expense.setDescription(editDesc.getValue().isBlank() ? null : editDesc.getValue().trim());
                expenseService.saveExpense(expense);
                refreshGrid();
                dialog.close();
                showNotification("Expense updated successfully.", NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                showNotification("Update failed: " + ex.getMessage(), NotificationVariant.LUMO_ERROR);
            }
        });

        Button cancelBtn = new Button("Cancel", e -> dialog.close());
        cancelBtn.getStyle()
                 .set("background", "transparent")
                 .set("color", TEXT_MUTED)
                 .set("border", "none")
                 .set("cursor", "pointer");

        dialog.getFooter().add(cancelBtn, saveBtn);
        dialog.open();
    }

    private void confirmDelete(Expense expense) {
        ConfirmDialog confirm = new ConfirmDialog();
        confirm.setHeader("Delete Expense");
        confirm.setText("Are you sure you want to delete expense ID "
                        + expense.getId() + " (" + expense.getCategory() + ", "
                        + formatInr(expense.getAmount()) + ")? This cannot be undone.");

        confirm.setCancelable(true);
        confirm.setCancelText("Cancel");
        confirm.setConfirmText("Delete");
        confirm.setConfirmButtonTheme("error primary");

        confirm.addConfirmListener(e -> {
            try {
                expenseService.deleteExpense(expense.getId());
                refreshGrid();
                showNotification("Expense ID " + expense.getId() + " deleted.", NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                showNotification("Delete failed: " + ex.getMessage(), NotificationVariant.LUMO_ERROR);
            }
        });

        confirm.open();
    }

    private void refreshGrid() {
        List<Expense> updated = expenseService.getAllExpenses();
        dataProvider = new ListDataProvider<>(updated);
        expenseGrid.setDataProvider(dataProvider);
        clearFilters();
    }

    private String formatInr(BigDecimal amount) {
        if (amount == null) return "₹0.00";
        return INR_FORMAT.format(amount.setScale(2, RoundingMode.HALF_UP));
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification n = Notification.show(message, 4000, Notification.Position.TOP_END);
        n.addThemeVariants(variant);
    }
}
