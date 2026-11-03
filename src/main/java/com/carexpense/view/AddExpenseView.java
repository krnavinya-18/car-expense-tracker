package com.carexpense.view;

import com.carexpense.model.Expense;
import com.carexpense.service.ExpenseService;
import com.vaadin.flow.component.button.Button;
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
 * Designed with a solid dark navy blue theme (#0B1F4D), white card container,
 * consistent typography, and properly aligned inputs and buttons.
 */
@PageTitle("Add Expense — Car Expense Tracker")
@Route(value = "add-expense", layout = MainLayout.class)
public class AddExpenseView extends VerticalLayout {

    private final ExpenseService expenseService;

    // Design constants
    private static final String NAVY        = "#0B1F4D";
    private static final String TEXT_MUTED  = "#64748B";
    private static final String CARD_BG     = "#FFFFFF";
    private static final String CARD_BORDER = "1px solid #E2E8F0";
    private static final String CARD_SHADOW = "0 2px 8px rgba(11, 31, 77, 0.05)";

    // Form fields
    private DatePicker        dateField;
    private ComboBox<String>  categoryField;
    private BigDecimalField   amountField;
    private TextArea          descriptionField;

    public AddExpenseView(ExpenseService expenseService) {
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
        content.setMaxWidth("700px");
        content.getStyle()
               .set("margin", "0 auto")
               .set("padding", "2rem 1.5rem");
        content.setSpacing(true);

        content.add(createHeader());
        content.add(createFormCard());

        add(content);
    }

    private Div createHeader() {
        H2 title = new H2("Register New Expense");
        title.getStyle()
             .set("color", NAVY)
             .set("font-size", "1.5rem")
             .set("font-weight", "700")
             .set("margin", "0 0 0.25rem 0")
             .set("letter-spacing", "-0.3px");

        Span subtitle = new Span("Enter the details below to record a new car expense into the database.");
        subtitle.getStyle()
                .set("color", TEXT_MUTED)
                .set("font-size", "0.88rem")
                .set("display", "block");

        Div header = new Div(title, subtitle);
        header.setWidthFull();
        header.getStyle().set("margin-bottom", "1rem");
        return header;
    }

    private Div createFormCard() {
        // Date field
        dateField = new DatePicker("Expense Date");
        dateField.setPlaceholder("Select date");
        dateField.setValue(LocalDate.now());
        dateField.setRequired(true);
        dateField.setWidthFull();
        dateField.setMax(LocalDate.now());

        // Category dropdown
        categoryField = new ComboBox<>("Expense Category");
        categoryField.setItems(ExpenseService.getAllCategories());
        categoryField.setPlaceholder("Select category");
        categoryField.setRequired(true);
        categoryField.setWidthFull();
        categoryField.setClearButtonVisible(true);

        // Amount field
        amountField = new BigDecimalField("Amount (₹)");
        amountField.setPlaceholder("0.00");
        amountField.setRequired(true);
        amountField.setWidthFull();
        amountField.setPrefixComponent(new Span("₹"));

        // Description textarea
        descriptionField = new TextArea("Description");
        descriptionField.setPlaceholder("Optional notes or details about this expense...");
        descriptionField.setWidthFull();
        descriptionField.setMaxLength(500);
        descriptionField.setHeight("110px");
        descriptionField.setHelperText("Maximum 500 characters");

        // Two-column row for date and category
        HorizontalLayout row1 = new HorizontalLayout(dateField, categoryField);
        row1.setWidthFull();
        row1.setSpacing(true);
        row1.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.START);

        // Buttons
        Button saveBtn  = buildSaveButton();
        Button clearBtn = buildClearButton();

        HorizontalLayout buttons = new HorizontalLayout(saveBtn, clearBtn);
        buttons.setSpacing(true);
        buttons.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        buttons.getStyle().set("margin-top", "1rem");

        VerticalLayout form = new VerticalLayout(
                row1,
                amountField,
                descriptionField,
                buttons
        );
        form.setPadding(false);
        form.setSpacing(true);

        Div card = new Div(form);
        card.setWidthFull();
        card.getStyle()
            .set("background", CARD_BG)
            .set("border", CARD_BORDER)
            .set("border-radius", "10px")
            .set("box-shadow", CARD_SHADOW)
            .set("padding", "2rem");
        return card;
    }

    private Button buildSaveButton() {
        Button btn = new Button("Save Expense", VaadinIcon.CHECK.create());
        btn.getStyle()
           .set("background", NAVY)
           .set("color", "#FFFFFF")
           .set("font-weight", "600")
           .set("font-size", "0.88rem")
           .set("border-radius", "6px")
           .set("padding", "0.65rem 1.6rem")
           .set("border", "none")
           .set("cursor", "pointer");
        btn.addClickListener(e -> handleSave());
        return btn;
    }

    private Button buildClearButton() {
        Button btn = new Button("Clear Form", VaadinIcon.CLOSE.create());
        btn.getStyle()
           .set("background", "transparent")
           .set("color", NAVY)
           .set("border", "1px solid #CBD5E1")
           .set("font-weight", "500")
           .set("font-size", "0.88rem")
           .set("border-radius", "6px")
           .set("padding", "0.65rem 1.25rem")
           .set("cursor", "pointer");
        btn.addClickListener(e -> clearForm());
        return btn;
    }

    private void handleSave() {
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

    private void clearForm() {
        dateField.setValue(LocalDate.now());
        dateField.setInvalid(false);
        categoryField.clear();
        categoryField.setInvalid(false);
        amountField.clear();
        amountField.setInvalid(false);
        descriptionField.clear();
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message, 4000, Notification.Position.TOP_END);
        notification.addThemeVariants(variant);
    }
}
