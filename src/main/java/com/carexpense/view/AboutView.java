package com.carexpense.view;

import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * About page — provides comprehensive project details, architecture overview,
 * and key feature descriptions.
 *
 * Adheres strictly to the unified Dark Navy Blue (#0B1F4D) design system.
 */
@PageTitle("About — Car Expense Tracker")
@Route(value = "about", layout = MainLayout.class)
public class AboutView extends VerticalLayout {

    private static final String NAVY        = "#0B1F4D";
    private static final String TEXT_MUTED  = "#64748B";
    private static final String CARD_BG     = "#FFFFFF";
    private static final String CARD_BORDER = "1px solid #E2E8F0";
    private static final String CARD_SHADOW = "0 2px 8px rgba(11, 31, 77, 0.05)";
    private static final String ICON_BG     = "#EEF2F9";

    public AboutView() {
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
        content.add(createOverviewCard());
        content.add(createTwoColumnSection());
        content.add(createTechStackCard());

        add(content);
    }

    private Div createHeader() {
        H2 title = new H2("About Car Expense Tracker");
        title.getStyle()
             .set("color", NAVY)
             .set("font-size", "1.5rem")
             .set("font-weight", "700")
             .set("margin", "0 0 0.25rem 0")
             .set("letter-spacing", "-0.3px");

        Span subtitle = new Span("System overview, core architectural features, and technology specifications.");
        subtitle.getStyle()
                .set("color", TEXT_MUTED)
                .set("font-size", "0.88rem")
                .set("display", "block");

        Div header = new Div(title, subtitle);
        header.setWidthFull();
        header.getStyle().set("margin-bottom", "1rem");
        return header;
    }

    private Div createOverviewCard() {
        Span sectionTitle = new Span("Project Overview");
        sectionTitle.getStyle()
                    .set("font-size", "1.05rem")
                    .set("font-weight", "600")
                    .set("color", NAVY);

        Div header = new Div(sectionTitle);
        header.getStyle()
              .set("padding", "1rem 1.25rem")
              .set("border-bottom", "1px solid #E2E8F0");

        Paragraph p1 = new Paragraph(
            "The Car Expense Tracking System is an enterprise-grade web application built to streamline the monitoring, " +
            "categorization, and analytical reporting of vehicle maintenance and operational expenditures. Designed specifically " +
            "for automotive enthusiasts, fleet managers, and individual vehicle owners, the platform enforces strict data integrity " +
            "and provides instantaneous visibility into running costs."
        );
        p1.getStyle().set("color", "#334155").set("font-size", "0.92rem").set("line-height", "1.6").set("margin", "0 0 0.75rem 0");

        Paragraph p2 = new Paragraph(
            "All application interfaces and reactive views are constructed entirely in Java using Vaadin Flow, completely removing the " +
            "need for manual client-side JavaScript or HTML templating. Persistence is backed by high-performance relational databases " +
            "(Oracle Database and embedded H2 for development) through Spring Data JPA."
        );
        p2.getStyle().set("color", "#334155").set("font-size", "0.92rem").set("line-height", "1.6").set("margin", "0");

        Div body = new Div(p1, p2);
        body.getStyle().set("padding", "1.25rem");

        Div card = new Div(header, body);
        card.setWidthFull();
        card.getStyle()
            .set("background", CARD_BG)
            .set("border", CARD_BORDER)
            .set("border-radius", "10px")
            .set("box-shadow", CARD_SHADOW)
            .set("margin-bottom", "1rem");
        return card;
    }

    private Div createTwoColumnSection() {
        Div leftCard  = createFeaturesCard();
        Div rightCard = createModulesCard();

        Div container = new Div(leftCard, rightCard);
        container.setWidthFull();
        container.getStyle()
                 .set("display", "grid")
                 .set("grid-template-columns", "repeat(auto-fit, minmax(360px, 1fr))")
                 .set("gap", "1rem")
                 .set("margin-bottom", "1rem");
        return container;
    }

    private Div createFeaturesCard() {
        Span title = new Span("Key Capabilities");
        title.getStyle().set("font-size", "1.05rem").set("font-weight", "600").set("color", NAVY);

        Div header = new Div(title);
        header.getStyle().set("padding", "1rem 1.25rem").set("border-bottom", "1px solid #E2E8F0");

        VerticalLayout list = new VerticalLayout();
        list.setPadding(false);
        list.setSpacing(true);
        list.getStyle().set("padding", "1.25rem");

        list.add(createFeatureItem(VaadinIcon.CHART_LINE, "Real-Time Financial Dashboard", "Calculates live metrics including total expenditure, average expense, and top spending category directly from database records."));
        list.add(createFeatureItem(VaadinIcon.FORM, "Validated Expense Entry", "Multi-category entry with constraint validation, date limits, and Indian Rupee (₹) currency formatting."));
        list.add(createFeatureItem(VaadinIcon.SEARCH, "Advanced Search & Filtering", "Full-text querying across descriptions and categories, alongside comprehensive date range filtering."));
        list.add(createFeatureItem(VaadinIcon.PIE_BAR_CHART, "Visual Cost Breakdown", "Categorical spending distributions and monthly expenditure trends visualized through Java-rendered components."));

        Div card = new Div(header, list);
        card.getStyle()
            .set("background", CARD_BG)
            .set("border", CARD_BORDER)
            .set("border-radius", "10px")
            .set("box-shadow", CARD_SHADOW);
        return card;
    }

    private HorizontalLayout createFeatureItem(VaadinIcon iconType, String heading, String desc) {
        Icon icon = iconType.create();
        icon.setSize("1.2rem");
        icon.getStyle().set("color", NAVY);

        Div iconBox = new Div(icon);
        iconBox.getStyle()
               .set("background", ICON_BG)
               .set("border-radius", "6px")
               .set("width", "36px")
               .set("height", "36px")
               .set("display", "flex")
               .set("align-items", "center")
               .set("justify-content", "center")
               .set("flex-shrink", "0");

        Span head = new Span(heading);
        head.getStyle().set("font-weight", "600").set("font-size", "0.88rem").set("color", NAVY).set("display", "block");

        Span text = new Span(desc);
        text.getStyle().set("font-size", "0.82rem").set("color", TEXT_MUTED).set("display", "block");

        VerticalLayout textLayout = new VerticalLayout(head, text);
        textLayout.setPadding(false);
        textLayout.setSpacing(false);

        HorizontalLayout row = new HorizontalLayout(iconBox, textLayout);
        row.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.START);
        row.setSpacing(true);
        row.setWidthFull();
        row.getStyle().set("margin-bottom", "0.5rem");
        return row;
    }

    private Div createModulesCard() {
        Span title = new Span("Application Modules");
        title.getStyle().set("font-size", "1.05rem").set("font-weight", "600").set("color", NAVY);

        Div header = new Div(title);
        header.getStyle().set("padding", "1rem 1.25rem").set("border-bottom", "1px solid #E2E8F0");

        VerticalLayout list = new VerticalLayout();
        list.setPadding(false);
        list.setSpacing(true);
        list.getStyle().set("padding", "1.25rem");

        list.add(createModuleItem("Dashboard Module", "Central command center displaying high-level statistical aggregates, top category analysis, and the 5 most recent expense entries."));
        list.add(createModuleItem("Registration Module", "Secure form input allowing instant database persistence for 9 categorized expenses including Fuel, Maintenance, and Taxes."));
        list.add(createModuleItem("Management Module", "Interactive data grid equipped with sortable headers, search filters, modal editor, and delete confirmation dialogs."));
        list.add(createModuleItem("Reporting Module", "Analytical summaries detailing monthly spending curves, percentage breakdown tables, and highest category tracking."));

        Div card = new Div(header, list);
        card.getStyle()
            .set("background", CARD_BG)
            .set("border", CARD_BORDER)
            .set("border-radius", "10px")
            .set("box-shadow", CARD_SHADOW);
        return card;
    }

    private VerticalLayout createModuleItem(String title, String desc) {
        Span head = new Span(title);
        head.getStyle().set("font-weight", "600").set("font-size", "0.88rem").set("color", NAVY).set("display", "block");

        Span text = new Span(desc);
        text.getStyle().set("font-size", "0.82rem").set("color", TEXT_MUTED).set("display", "block").set("line-height", "1.4");

        VerticalLayout item = new VerticalLayout(head, text);
        item.setPadding(false);
        item.setSpacing(false);
        item.getStyle().set("margin-bottom", "0.6rem");
        return item;
    }

    private Div createTechStackCard() {
        Span title = new Span("Technology Stack Specifications");
        title.getStyle().set("font-size", "1.05rem").set("font-weight", "600").set("color", NAVY);

        Div header = new Div(title);
        header.getStyle().set("padding", "1rem 1.25rem").set("border-bottom", "1px solid #E2E8F0");

        Div grid = new Div();
        grid.getStyle()
            .set("padding", "1.25rem")
            .set("display", "grid")
            .set("grid-template-columns", "repeat(auto-fit, minmax(220px, 1fr))")
            .set("gap", "1rem");

        grid.add(createStackPill("Java 21 LTS", "Core Programming Language"));
        grid.add(createStackPill("Spring Boot 3.3.5", "Backend Enterprise Framework"));
        grid.add(createStackPill("Vaadin Flow 24.4", "Java-based Web UI Engine"));
        grid.add(createStackPill("Spring Data JPA", "Object-Relational Mapping"));
        grid.add(createStackPill("Oracle Database", "Primary Production Database"));
        grid.add(createStackPill("H2 Database", "Development In-Memory Database"));
        grid.add(createStackPill("Apache Maven 3.9", "Dependency & Build Automation"));
        grid.add(createStackPill("Git / GitHub", "Version Control Management"));

        Div card = new Div(header, grid);
        card.setWidthFull();
        card.getStyle()
            .set("background", CARD_BG)
            .set("border", CARD_BORDER)
            .set("border-radius", "10px")
            .set("box-shadow", CARD_SHADOW);
        return card;
    }

    private Div createStackPill(String name, String role) {
        Span nameSpan = new Span(name);
        nameSpan.getStyle().set("font-weight", "600").set("font-size", "0.88rem").set("color", NAVY).set("display", "block");

        Span roleSpan = new Span(role);
        roleSpan.getStyle().set("font-size", "0.78rem").set("color", TEXT_MUTED).set("display", "block");

        Div pill = new Div(nameSpan, roleSpan);
        pill.getStyle()
            .set("background", "#F8FAFC")
            .set("border", "1px solid #E2E8F0")
            .set("border-left", "3px solid " + NAVY)
            .set("border-radius", "6px")
            .set("padding", "0.75rem 1rem");
        return pill;
    }
}
