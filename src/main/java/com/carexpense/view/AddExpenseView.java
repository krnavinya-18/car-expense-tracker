package com.carexpense.view;

import com.carexpense.model.Expense;
import com.carexpense.service.ExpenseService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Add Expense view — form for registering a new car expense.
 *
 * <p>Provides a validated form with date picker, category selector,
 * amount field, and description textarea. On successful save the record
 * is persisted to Oracle Database. Appropriate success/error notifications
 * are shown after each action.</p>
 */
@PageTitle("Add Expense — Car Expense Tracker")
@Route(value = "add-expense", layout = MainLayout.class)
public class AddExpenseView extends VerticalLayout {

    private final ExpenseService expenseService;

    // Form fields
    private DatePicker        dateField;
    private ComboBox<String>  categoryField;
    private BigDecimalField   amountField;
    private TextArea          descriptionField;

    /**
     * Constructs the Add Expense view.
     *
     * @param expenseService the service used to persist expense records
     */
    public AddExpenseView(ExpenseService expenseService) {
        this.expenseService = expenseService;
        buildUI();
    }

    // -------------------------------------------------------
    // UI Construction
    // -------------------------------------------------------

    /**
     * Assembles the complete form layout.
     */
    private void buildUI() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", "#f4f6fb");

        VerticalLayout content = new VerticalLayout();
        content.setMaxWidth("720px");
        content.getStyle()
               .set("margin", "0 auto")
               .set("padding", "1.5rem");
        content.setSpacing(true);

        content.add(createPageHeader());
        content.add(createFormCard());

        add(content);
    }

    /**
     * Creates the page title header area.
     */
    private Div createPageHeader() {
        H2 title = new H2("Register New Expense");
        title.getStyle()
             .set("color", "#1a237e")
             .set("font-size", "1.6rem")
             .set("font-weight", "700")
             .set("margin", "0 0 0.25rem 0");

        Span subtitle = new Span("Fill in the details below to record a new car expense.");
        subtitle.getStyle()
                .set("color", "#546e7a")
                .set("font-size", "0.9rem");

        Div header = new Div(title, subtitle);
        header.getStyle().set("margin-bottom", "0.5rem");
        return header;
    }

    /**
     * Creates the white card containing the expense registration form.
     */
    private Div createFormCard() {
        // --- Date field ---
        dateField = new DatePicker("Expense Date");
        dateField.setPlaceholder("Select date");
        dateField.setValue(LocalDate.now());
        dateField.setRequired(true);
        dateField.setWidthFull();
        dateField.setMax(LocalDate.now());  // cannot enter future dates

        // --- Category dropdown ---
        categoryField = new ComboBox<>("Expense Category");
        categoryField.setItems(ExpenseService.getAllCategories());
        categoryField.setPlaceholder("Select category");
        categoryField.setRequired(true);
        categoryField.setWidthFull();
        categoryField.setClearButtonVisible(true);

        // --- Amount field ---
        amountField = new BigDecimalField("Amount (₹)");
        amountField.setPlaceholder("0.00");
        amountField.setRequired(true);
        amountField.setWidthFull();
        amountField.setPrefixComponent(new Span("₹"));


        // --- Description textarea ---
        descriptionField = new TextArea("Description");
        descriptionField.setPlaceholder("Optional notes or details about this expense...");
        descriptionField.setWidthFull();
        descriptionField.setMaxLength(500);
        descriptionField.setHeight("120px");
        descriptionField.setHelperText("Maximum 500 characters");

        // Two-column row for date and category
        HorizontalLayout row1 = new HorizontalLayout(dateField, categoryField);
        row1.setWidthFull();
        row1.setSpacing(true);
        row1.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.START);

        // Two-column row for amount (left) and empty right space
        HorizontalLayout row2 = new HorizontalLayout(amountField);
        row2.setWidthFull();

        // Action buttons
        Button saveBtn  = buildSaveButton();
        Button clearBtn = buildClearButton();

        HorizontalLayout buttons = new HorizontalLayout(saveBtn, clearBtn);
        buttons.setSpacing(true);
        buttons.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        buttons.getStyle().set("margin-top", "0.75rem");

        // Form container
        VerticalLayout form = new VerticalLayout(
                createFieldLabel("Step 1 — Enter Expense Date and Category"),
                row1,
                createFieldLabel("Step 2 — Enter Amount and Description"),
                row2,
                descriptionField,
                createDivider(),
                buttons
        );
        form.setPadding(false);
        form.setSpacing(true);

        Div card = new Div(form);
        card.setWidthFull();
        card.getStyle()
            .set("background", "#ffffff")
            .set("border-radius", "14px")
            .set("box-shadow", "0 2px 16px rgba(0,0,0,0.09)")
            .set("padding", "1.75rem 2rem");
        return card;
    }

    /**
     * Creates a subtle section label divider within the form.
     */
    private Span createFieldLabel(String text) {
        Span label = new Span(text);
        label.getStyle()
             .set("font-size", "0.78rem")
             .set("font-weight", "700")
             .set("color", "#90a4ae")
             .set("text-transform", "uppercase")
             .set("letter-spacing", "0.8px")
             .set("display", "block")
             .set("margin-top", "0.5rem");
        return label;
    }

    /**
     * Creates a thin horizontal divider line.
     */
    private Hr createDivider() {
        Hr hr = new Hr();
        hr.getStyle()
          .set("border", "none")
          .set("border-top", "1px solid #e8eaf6")
          .set("margin", "0.5rem 0");
        return hr;
    }

    // -------------------------------------------------------
    // Buttons
    // -------------------------------------------------------

    /**
     * Creates and wires the Save Expense button.
     */
    private Button buildSaveButton() {
        Button btn = new Button("Save Expense", VaadinIcon.CHECK.create());
        btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btn.getStyle()
           .set("background", "linear-gradient(135deg, #1a237e, #1565c0)")
           .set("color", "#fff")
           .set("font-weight", "600")
           .set("border-radius", "8px")
           .set("padding", "0.6rem 1.5rem")
           .set("cursor", "pointer");
        btn.addClickListener(e -> handleSave());
        return btn;
    }

    /**
     * Creates and wires the Clear Form button.
     */
    private Button buildClearButton() {
        Button btn = new Button("Clear Form", VaadinIcon.CLOSE.create());
        btn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btn.getStyle()
           .set("color", "#546e7a")
           .set("border-radius", "8px")
           .set("padding", "0.6rem 1.2rem");
        btn.addClickListener(e -> clearForm());
        return btn;
    }

    // -------------------------------------------------------
    // Event Handlers
    // -------------------------------------------------------

    /**
     * Validates form fields and saves the expense to the database.
     * Shows success or error notifications accordingly.
     */
    private void handleSave() {
        // --- Validation ---
        boolean valid = true;

        if (dateField.isEmpty()) {
            dateField.setInvalid(true);
            dateField.setErrorMessage("Please select an expense date.");
            valid = false;
        } else {
            dateField.setInvalid(false);
        }

        if (categoryField.isEmpty()) {
            categoryField.setInvalid(true);
            categoryField.setErrorMessage("Please select a category.");
            valid = false;
        } else {
            categoryField.setInvalid(false);
        }

        if (amountField.isEmpty()) {
            amountField.setInvalid(true);
            amountField.setErrorMessage("Please enter the expense amount.");
            valid = false;
        } else if (amountField.getValue() != null &&
                   amountField.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            amountField.setInvalid(true);
            amountField.setErrorMessage("Amount must be greater than ₹0.");
            valid = false;
        } else {
            amountField.setInvalid(false);
        }

        if (!valid) {
            showNotification("Please correct the highlighted fields before saving.",
                             NotificationVariant.LUMO_ERROR);
            return;
        }

        // --- Build and persist entity ---
        try {
            Expense expense = new Expense(
                    dateField.getValue(),
                    categoryField.getValue(),
                    amountField.getValue(),
                    descriptionField.getValue().isBlank() ? null : descriptionField.getValue().trim()
            );

            expenseService.saveExpense(expense);
            showNotification("Expense saved successfully! (ID: " + expense.getId() + ")",
                             NotificationVariant.LUMO_SUCCESS);
            clearForm();

        } catch (Exception ex) {
            showNotification("Error saving expense: " + ex.getMessage(),
                             NotificationVariant.LUMO_ERROR);
        }
    }

    /**
     * Resets all form fields to their default/empty state.
     */
    private void clearForm() {
        dateField.setValue(LocalDate.now());
        dateField.setInvalid(false);
        categoryField.clear();
        categoryField.setInvalid(false);
        amountField.clear();
        amountField.setInvalid(false);
        descriptionField.clear();
    }

    // -------------------------------------------------------
    // Utility
    // -------------------------------------------------------

    /**
     * Displays a Vaadin notification with the given message and styling variant.
     *
     * @param message the text to display
     * @param variant the notification style (success, error, etc.)
     */
    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message, 4000, Notification.Position.TOP_END);
        notification.addThemeVariants(variant);
    }
}
