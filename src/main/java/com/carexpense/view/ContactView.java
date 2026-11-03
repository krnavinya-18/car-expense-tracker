package com.carexpense.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Contact and Support page — academic project contact information and
 * interactive user feedback submission.
 *
 * Adheres strictly to the unified Dark Navy Blue (#0B1F4D) design system.
 */
@PageTitle("Contact & Support — Car Expense Tracker")
@Route(value = "contact", layout = MainLayout.class)
public class ContactView extends VerticalLayout {

    private static final String NAVY        = "#0B1F4D";
    private static final String TEXT_MUTED  = "#64748B";
    private static final String CARD_BG     = "#FFFFFF";
    private static final String CARD_BORDER = "1px solid #E2E8F0";
    private static final String CARD_SHADOW = "0 2px 8px rgba(11, 31, 77, 0.05)";
    private static final String ICON_BG     = "#EEF2F9";

    // Feedback form fields
    private TextField  nameField;
    private EmailField emailField;
    private TextField  subjectField;
    private TextArea   messageField;

    public ContactView() {
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
        content.add(createMainSection());

        add(content);
    }

    private Div createHeader() {
        H2 title = new H2("Contact & Support");
        title.getStyle()
             .set("color", NAVY)
             .set("font-size", "1.5rem")
             .set("font-weight", "700")
             .set("margin", "0 0 0.25rem 0")
             .set("letter-spacing", "-0.3px");

        Span subtitle = new Span("Project communication details, academic submission info, and feedback form.");
        subtitle.getStyle()
                .set("color", TEXT_MUTED)
                .set("font-size", "0.88rem")
                .set("display", "block");

        Div header = new Div(title, subtitle);
        header.setWidthFull();
        header.getStyle().set("margin-bottom", "1rem");
        return header;
    }

    private Div createMainSection() {
        Div leftColumn  = createContactInfoColumn();
        Div rightColumn = createFeedbackFormCard();

        Div container = new Div(leftColumn, rightColumn);
        container.setWidthFull();
        container.getStyle()
                 .set("display", "grid")
                 .set("grid-template-columns", "repeat(auto-fit, minmax(360px, 1fr))")
                 .set("gap", "1.25rem");
        return container;
    }

    private Div createContactInfoColumn() {
        Div col = new Div();
        col.getStyle()
           .set("display", "flex")
           .set("flex-direction", "column")
           .set("gap", "1rem");

        // Project / Academic Information Card
        Div academicCard = new Div();
        academicCard.setWidthFull();
        academicCard.getStyle()
                    .set("background", CARD_BG)
                    .set("border", CARD_BORDER)
                    .set("border-radius", "10px")
                    .set("box-shadow", CARD_SHADOW);

        Span title1 = new Span("Academic & Project Information");
        title1.getStyle().set("font-size", "1.05rem").set("font-weight", "600").set("color", NAVY);
        Div h1 = new Div(title1);
        h1.getStyle().set("padding", "1rem 1.25rem").set("border-bottom", "1px solid #E2E8F0");

        VerticalLayout b1 = new VerticalLayout();
        b1.setPadding(false);
        b1.setSpacing(true);
        b1.getStyle().set("padding", "1.25rem");

        b1.add(createInfoRow(VaadinIcon.ACADEMY_CAP, "Course / Degree", "Bachelor of Engineering / Technology (CSE / IT)"));
        b1.add(createInfoRow(VaadinIcon.FILE_TEXT, "Project Scope", "Database Management & Web Systems Mini-Project"));
        b1.add(createInfoRow(VaadinIcon.USER_CHECK, "Faculty Evaluation", "Department of Computer Science & Engineering"));
        b1.add(createInfoRow(VaadinIcon.DATABASE, "Target Environment", "Oracle Database 21c XE / Local Spring Boot Server"));

        academicCard.add(h1, b1);

        // Channels Card
        Div channelsCard = new Div();
        channelsCard.setWidthFull();
        channelsCard.getStyle()
                    .set("background", CARD_BG)
                    .set("border", CARD_BORDER)
                    .set("border-radius", "10px")
                    .set("box-shadow", CARD_SHADOW);

        Span title2 = new Span("Direct Communication Channels");
        title2.getStyle().set("font-size", "1.05rem").set("font-weight", "600").set("color", NAVY);
        Div h2 = new Div(title2);
        h2.getStyle().set("padding", "1rem 1.25rem").set("border-bottom", "1px solid #E2E8F0");

        VerticalLayout b2 = new VerticalLayout();
        b2.setPadding(false);
        b2.setSpacing(true);
        b2.getStyle().set("padding", "1.25rem");

        b2.add(createInfoRow(VaadinIcon.ENVELOPE, "Project Email", "project.carexpense@example.edu"));
        b2.add(createInfoRow(VaadinIcon.CODE, "Source Code", "Local Workspace Repository (Git master branch)"));
        b2.add(createInfoRow(VaadinIcon.GLOBE, "Web Portal", "http://localhost:8080 (Embedded Tomcat / Vaadin Flow)"));

        channelsCard.add(h2, b2);

        col.add(academicCard, channelsCard);
        return col;
    }

    private HorizontalLayout createInfoRow(VaadinIcon iconType, String label, String value) {
        Icon icon = iconType.create();
        icon.setSize("1.1rem");
        icon.getStyle().set("color", NAVY);

        Div iconBox = new Div(icon);
        iconBox.getStyle()
               .set("background", ICON_BG)
               .set("border-radius", "6px")
               .set("width", "34px")
               .set("height", "34px")
               .set("display", "flex")
               .set("align-items", "center")
               .set("justify-content", "center")
               .set("flex-shrink", "0");

        Span lbl = new Span(label);
        lbl.getStyle().set("font-size", "0.76rem").set("font-weight", "600").set("color", TEXT_MUTED).set("text-transform", "uppercase");

        Span val = new Span(value);
        val.getStyle().set("font-size", "0.86rem").set("font-weight", "500").set("color", "#1E293B");

        VerticalLayout textLayout = new VerticalLayout(lbl, val);
        textLayout.setPadding(false);
        textLayout.setSpacing(false);

        HorizontalLayout row = new HorizontalLayout(iconBox, textLayout);
        row.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        row.setSpacing(true);
        row.setWidthFull();
        row.getStyle().set("margin-bottom", "0.4rem");
        return row;
    }

    private Div createFeedbackFormCard() {
        Span title = new Span("Send Message / Feedback");
        title.getStyle().set("font-size", "1.05rem").set("font-weight", "600").set("color", NAVY);

        Div header = new Div(title);
        header.getStyle().set("padding", "1rem 1.25rem").set("border-bottom", "1px solid #E2E8F0");

        nameField = new TextField("Your Name");
        nameField.setPlaceholder("Enter full name");
        nameField.setRequired(true);
        nameField.setWidthFull();

        emailField = new EmailField("Email Address");
        emailField.setPlaceholder("name@domain.edu");
        emailField.setRequired(true);
        emailField.setWidthFull();

        subjectField = new TextField("Subject");
        subjectField.setPlaceholder("Query topic or feedback summary");
        subjectField.setRequired(true);
        subjectField.setWidthFull();

        messageField = new TextArea("Message");
        messageField.setPlaceholder("Type your message, issue, or faculty inspection comment here...");
        messageField.setRequired(true);
        messageField.setWidthFull();
        messageField.setHeight("130px");
        messageField.setMaxLength(1000);

        Button submitBtn = new Button("Submit Message", VaadinIcon.PAPERPLANE.create());
        submitBtn.getStyle()
                 .set("background", NAVY)
                 .set("color", "#FFFFFF")
                 .set("font-weight", "600")
                 .set("font-size", "0.88rem")
                 .set("border-radius", "6px")
                 .set("padding", "0.65rem 1.5rem")
                 .set("border", "none")
                 .set("cursor", "pointer");
        submitBtn.addClickListener(e -> handleSubmit());

        Button resetBtn = new Button("Clear Form", VaadinIcon.CLOSE.create());
        resetBtn.getStyle()
                .set("background", "transparent")
                .set("color", NAVY)
                .set("border", "1px solid #CBD5E1")
                .set("font-weight", "500")
                .set("font-size", "0.88rem")
                .set("border-radius", "6px")
                .set("padding", "0.65rem 1.2rem")
                .set("cursor", "pointer");
        resetBtn.addClickListener(e -> clearForm());

        HorizontalLayout buttons = new HorizontalLayout(submitBtn, resetBtn);
        buttons.setSpacing(true);
        buttons.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        buttons.getStyle().set("margin-top", "0.75rem");

        VerticalLayout form = new VerticalLayout(
                nameField,
                emailField,
                subjectField,
                messageField,
                buttons
        );
        form.setPadding(false);
        form.setSpacing(true);
        form.getStyle().set("padding", "1.25rem");

        Div card = new Div(header, form);
        card.setWidthFull();
        card.getStyle()
            .set("background", CARD_BG)
            .set("border", CARD_BORDER)
            .set("border-radius", "10px")
            .set("box-shadow", CARD_SHADOW);
        return card;
    }

    private void handleSubmit() {
        boolean valid = true;

        if (nameField.isEmpty()) {
            nameField.setInvalid(true);
            nameField.setErrorMessage("Name is required.");
            valid = false;
        } else {
            nameField.setInvalid(false);
        }

        if (emailField.isEmpty() || emailField.isInvalid()) {
            emailField.setInvalid(true);
            emailField.setErrorMessage("Valid email address is required.");
            valid = false;
        } else {
            emailField.setInvalid(false);
        }

        if (subjectField.isEmpty()) {
            subjectField.setInvalid(true);
            subjectField.setErrorMessage("Subject is required.");
            valid = false;
        } else {
            subjectField.setInvalid(false);
        }

        if (messageField.isEmpty()) {
            messageField.setInvalid(true);
            messageField.setErrorMessage("Message content is required.");
            valid = false;
        } else {
            messageField.setInvalid(false);
        }

        if (!valid) {
            Notification note = Notification.show("Please complete all required fields.", 3500, Notification.Position.TOP_END);
            note.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        Notification note = Notification.show(
                "Message submitted successfully! Thank you for contacting us.",
                4500,
                Notification.Position.TOP_END
        );
        note.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        clearForm();
    }

    private void clearForm() {
        nameField.clear();
        nameField.setInvalid(false);
        emailField.clear();
        emailField.setInvalid(false);
        subjectField.clear();
        subjectField.setInvalid(false);
        messageField.clear();
        messageField.setInvalid(false);
    }
}
