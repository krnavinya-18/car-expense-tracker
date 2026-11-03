package com.carexpense.view;

import com.carexpense.model.Expense;
import com.carexpense.service.ExpenseService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import java.util.Optional;

/**
 * View Expenses page — a searchable, filterable, sortable expense table.
 *
 * <p>Displays all expense records with support for:
 * <ul>
 *   <li>Full-text search by category or description</li>
 *   <li>Filter by category or date range</li>
 *   <li>Column sorting</li>
 *   <li>Inline edit via a modal dialog</li>
 *   <li>Delete with confirmation</li>
 *   <li>Vaadin built-in pagination</li>
 * </ul>
 * </p>
 */
@PageTitle("View Expenses — Car Expense Tracker")
@Route(value = "view-expenses", layout = MainLayout.class)
public class ViewExpensesView extends VerticalLayout {

    private final ExpenseService expenseService;

    // Grid and its data provider
    private Grid<Expense>            expenseGrid;
    private ListDataProvider<Expense> dataProvider;

    // Filter controls
    private TextField        searchField;
    private ComboBox<String> categoryFilter;
    private DatePicker       dateFrom;
    private DatePicker       dateTo;

    // Formatting helpers
    private static final NumberFormat    INR_FORMAT = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
    private static final DateTimeFormatter DATE_FMT  = DateTimeFormatter.ofPattern("dd MMM yyyy");

    /**
     * Constructs the View Expenses page.
     *
     * @param expenseService the service for expense CRUD operations
     */
    public ViewExpensesView(ExpenseService expenseService) {
        this.expenseService = expenseService;
        buildUI();
    }

    // -------------------------------------------------------
    // UI Construction
    // -------------------------------------------------------

    /**
     * Assembles the full page layout.
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

        content.add(createPageHeader());
        content.add(createFiltersCard());
        content.add(createGridCard());

        add(content);
    }

    /**
     * Page header with title.
     */
    private Div createPageHeader() {
        H2 title = new H2("All Expenses");
        title.getStyle()
             .set("color", "#1a237e")
             .set("font-size", "1.6rem")
             .set("font-weight", "700")
             .set("margin", "0 0 0.25rem 0");

        Span subtitle = new Span("Search, filter, edit, or delete your car expense records.");
        subtitle.getStyle().set("color", "#546e7a").set("font-size", "0.9rem");

        Div header = new Div(title, subtitle);
        header.getStyle().set("margin-bottom", "0.5rem");
        return header;
    }

    // -------------------------------------------------------
    // Filter Card
    // -------------------------------------------------------

    /**
     * Creates the white filter controls card above the table.
     */
    private Div createFiltersCard() {
        // Search field
        searchField = new TextField();
        searchField.setPlaceholder("Search by category or description...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);
        searchField.setWidth("300px");
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> applyFilters());

        // Category filter
        categoryFilter = new ComboBox<>("Category");
        List<String> categories = ExpenseService.getAllCategories();
        categoryFilter.setItems(categories);
        categoryFilter.setPlaceholder("All Categories");
        categoryFilter.setClearButtonVisible(true);
        categoryFilter.setWidth("200px");
        categoryFilter.addValueChangeListener(e -> applyFilters());

        // Date range
        dateFrom = new DatePicker("From Date");
        dateFrom.setWidth("160px");
        dateFrom.setClearButtonVisible(true);
        dateFrom.addValueChangeListener(e -> applyFilters());

        dateTo = new DatePicker("To Date");
        dateTo.setWidth("160px");
        dateTo.setClearButtonVisible(true);
        dateTo.addValueChangeListener(e -> applyFilters());

        Button clearFilterBtn = new Button("Clear Filters", VaadinIcon.CLOSE.create());
        clearFilterBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        clearFilterBtn.getStyle().set("align-self", "flex-end");
        clearFilterBtn.addClickListener(e -> clearFilters());

        HorizontalLayout filters = new HorizontalLayout(
                searchField, categoryFilter, dateFrom, dateTo, clearFilterBtn);
        filters.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        filters.getStyle().set("flex-wrap", "wrap");
        filters.setSpacing(true);
        filters.setWidthFull();

        Div card = new Div(filters);
        card.setWidthFull();
        card.getStyle()
            .set("background", "#ffffff")
            .set("border-radius", "12px")
            .set("padding", "1.25rem")
            .set("box-shadow", "0 2px 12px rgba(0,0,0,0.07)");
        return card;
    }

    // -------------------------------------------------------
    // Grid Card
    // -------------------------------------------------------

    /**
     * Creates the card containing the expense data grid.
     */
    private Div createGridCard() {
        expenseGrid = buildGrid();

        Div card = new Div(expenseGrid);
        card.setWidthFull();
        card.getStyle()
            .set("background", "#ffffff")
            .set("border-radius", "12px")
            .set("box-shadow", "0 2px 12px rgba(0,0,0,0.07)")
            .set("overflow", "hidden");
        card.setHeightFull();
        return card;
    }

    /**
     * Builds and configures the Vaadin Grid with all columns and data.
     */
    private Grid<Expense> buildGrid() {
        List<Expense> allExpenses = expenseService.getAllExpenses();
        dataProvider = new ListDataProvider<>(allExpenses);

        Grid<Expense> grid = new Grid<>(Expense.class, false);
        grid.setDataProvider(dataProvider);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS);
        grid.setSizeFull();
        grid.setPageSize(15);

        // Expense ID column
        grid.addColumn(Expense::getId)
            .setHeader("ID")
            .setWidth("80px")
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

        // Amount column with INR formatting
        grid.addColumn(e -> formatInr(e.getAmount()))
            .setHeader("Amount")
            .setWidth("140px")
            .setFlexGrow(0)
            .setSortable(true)
            .setComparator((a, b) -> a.getAmount().compareTo(b.getAmount()))
            .setKey("amount");

        // Description column
        grid.addColumn(e -> e.getDescription() != null ? e.getDescription() : "—")
            .setHeader("Description")
            .setFlexGrow(2)
            .setKey("description");

        // Actions column with Edit and Delete buttons
        grid.addColumn(new ComponentRenderer<>(this::buildActionsCell))
            .setHeader("Actions")
            .setWidth("160px")
            .setFlexGrow(0)
            .setKey("actions");

        // Style column headers
        grid.getColumns().forEach(col -> col.setResizable(true));

        return grid;
    }

    /**
     * Builds the Edit and Delete button pair for each grid row.
     */
    private HorizontalLayout buildActionsCell(Expense expense) {
        Button editBtn = new Button(VaadinIcon.EDIT.create());
        editBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        editBtn.getStyle().set("color", "#1565c0").set("cursor", "pointer");
        editBtn.setTooltipText("Edit this expense");
        editBtn.addClickListener(e -> openEditDialog(expense));

        Button deleteBtn = new Button(VaadinIcon.TRASH.create());
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR,
                                   ButtonVariant.LUMO_TERTIARY);
        deleteBtn.getStyle().set("cursor", "pointer");
        deleteBtn.setTooltipText("Delete this expense");
        deleteBtn.addClickListener(e -> confirmDelete(expense));

        HorizontalLayout actions = new HorizontalLayout(editBtn, deleteBtn);
        actions.setSpacing(false);
        actions.setPadding(false);
        return actions;
    }

    // -------------------------------------------------------
    // Filter Logic
    // -------------------------------------------------------

    /**
     * Applies active filters to the data provider.
     * All active criteria are combined with AND logic.
     */
    private void applyFilters() {
        dataProvider.setFilter(expense -> {
            String search   = searchField.getValue().trim().toLowerCase();
            String category = categoryFilter.getValue();
            LocalDate from  = dateFrom.getValue();
            LocalDate to    = dateTo.getValue();

            // Search filter
            if (!search.isEmpty()) {
                boolean matchCat  = expense.getCategory().toLowerCase().contains(search);
                boolean matchDesc = expense.getDescription() != null
                                    && expense.getDescription().toLowerCase().contains(search);
                if (!matchCat && !matchDesc) return false;
            }

            // Category filter
            if (category != null && !category.isBlank()) {
                if (!expense.getCategory().equalsIgnoreCase(category)) return false;
            }

            // Date range filter
            if (from != null && expense.getExpenseDate().isBefore(from)) return false;
            if (to   != null && expense.getExpenseDate().isAfter(to))   return false;

            return true;
        });
    }

    /**
     * Clears all filter controls and resets the grid to full data.
     */
    private void clearFilters() {
        searchField.clear();
        categoryFilter.clear();
        dateFrom.clear();
        dateTo.clear();
        dataProvider.clearFilters();
    }

    // -------------------------------------------------------
    // Edit Dialog
    // -------------------------------------------------------

    /**
     * Opens a modal dialog pre-populated with the selected expense's data for editing.
     *
     * @param expense the expense to edit
     */
    private void openEditDialog(Expense expense) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Edit Expense  (ID: " + expense.getId() + ")");
        dialog.setWidth("500px");
        dialog.setModal(true);
        dialog.setDraggable(true);

        // Form fields inside dialog
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
        editDesc.setHeight("100px");
        editDesc.setWidthFull();

        VerticalLayout dialogContent = new VerticalLayout(editDate, editCategory, editAmount, editDesc);
        dialogContent.setPadding(false);
        dialogContent.setSpacing(true);
        dialog.add(dialogContent);

        // Dialog action buttons
        Button saveBtn = new Button("Save Changes", VaadinIcon.CHECK.create());
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveBtn.addClickListener(e -> {
            // Validate
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
        cancelBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        dialog.getFooter().add(cancelBtn, saveBtn);
        dialog.open();
    }

    // -------------------------------------------------------
    // Delete Logic
    // -------------------------------------------------------

    /**
     * Shows a confirmation dialog before deleting an expense.
     *
     * @param expense the expense to delete
     */
    private void confirmDelete(Expense expense) {
        ConfirmDialog confirm = new ConfirmDialog();
        confirm.setHeader("Delete Expense");
        confirm.setText("Are you sure you want to permanently delete expense ID "
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

    // -------------------------------------------------------
    // Refresh
    // -------------------------------------------------------

    /**
     * Reloads all expense data from the database and refreshes the grid.
     */
    private void refreshGrid() {
        List<Expense> updated = expenseService.getAllExpenses();
        dataProvider = new ListDataProvider<>(updated);
        expenseGrid.setDataProvider(dataProvider);
        clearFilters();
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
     * Displays a Vaadin notification.
     */
    private void showNotification(String message, NotificationVariant variant) {
        Notification n = Notification.show(message, 4000, Notification.Position.TOP_END);
        n.addThemeVariants(variant);
    }
}
