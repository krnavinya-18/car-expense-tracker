package com.carexpense.view;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * Application shell and main layout for all views.
 *
 * <p>Provides the sidebar navigation, application header, and content
 * area. All page views are rendered inside the content slot of this layout.
 * Uses Vaadin AppLayout with a SideNav for professional navigation.</p>
 */
public class MainLayout extends AppLayout {

    /**
     * Constructs the main application layout with header and sidebar.
     */
    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        createHeader();
        createDrawer();
    }

    // -------------------------------------------------------
    // Header
    // -------------------------------------------------------

    /**
     * Creates the top navigation bar with a drawer toggle and app title.
     */
    private void createHeader() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getStyle()
              .set("color", "#ffffff")
              .set("cursor", "pointer");

        H1 appTitle = new H1("Car Expense Tracker");
        appTitle.getStyle()
                .set("font-size", "1.1rem")
                .set("font-weight", "600")
                .set("color", "#ffffff")
                .set("margin", "0")
                .set("letter-spacing", "0.3px");

        HorizontalLayout header = new HorizontalLayout(toggle, appTitle);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.setPadding(false);
        header.setSpacing(true);
        header.getStyle()
              .set("background", "linear-gradient(135deg, #1a237e 0%, #283593 100%)")
              .set("padding", "0 1rem")
              .set("box-shadow", "0 2px 8px rgba(0,0,0,0.3)");
        header.setHeight("60px");

        addToNavbar(true, header);
    }

    // -------------------------------------------------------
    // Drawer / Sidebar
    // -------------------------------------------------------

    /**
     * Creates the sidebar navigation drawer with branding and navigation links.
     */
    private void createDrawer() {
        // Brand / logo section
        Div brand = createBrandSection();

        // Navigation items
        SideNav nav = new SideNav();
        nav.setWidthFull();
        nav.getStyle()
           .set("padding", "0.5rem 0");

        SideNavItem dashboardItem = new SideNavItem(
                "Dashboard", DashboardView.class, VaadinIcon.DASHBOARD.create());

        SideNavItem addExpenseItem = new SideNavItem(
                "Add Expense", AddExpenseView.class, VaadinIcon.PLUS_CIRCLE.create());

        SideNavItem viewExpensesItem = new SideNavItem(
                "View Expenses", ViewExpensesView.class, VaadinIcon.LIST.create());

        SideNavItem reportsItem = new SideNavItem(
                "Reports", ReportsView.class, VaadinIcon.BAR_CHART.create());

        styleNavItem(dashboardItem);
        styleNavItem(addExpenseItem);
        styleNavItem(viewExpensesItem);
        styleNavItem(reportsItem);

        nav.addItem(dashboardItem, addExpenseItem, viewExpensesItem, reportsItem);

        // Footer section inside drawer
        Div footer = createDrawerFooter();

        VerticalLayout drawerContent = new VerticalLayout(brand, nav, footer);
        drawerContent.setSizeFull();
        drawerContent.setPadding(false);
        drawerContent.setSpacing(false);
        drawerContent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        drawerContent.getStyle()
                     .set("background", "linear-gradient(180deg, #1a237e 0%, #1565c0 100%)")
                     .set("min-height", "100vh");

        addToDrawer(drawerContent);
    }

    /**
     * Creates the brand/logo area at the top of the sidebar.
     */
    private Div createBrandSection() {
        Icon carIcon = VaadinIcon.CAR.create();
        carIcon.setSize("2rem");
        carIcon.getStyle().set("color", "#90caf9");

        H2 brandName = new H2("CarTracker");
        brandName.getStyle()
                 .set("color", "#ffffff")
                 .set("font-size", "1.2rem")
                 .set("font-weight", "700")
                 .set("margin", "0");

        Span tagline = new Span("Expense Management");
        tagline.getStyle()
               .set("color", "#90caf9")
               .set("font-size", "0.75rem")
               .set("letter-spacing", "0.5px");

        VerticalLayout textSection = new VerticalLayout(brandName, tagline);
        textSection.setPadding(false);
        textSection.setSpacing(false);

        HorizontalLayout brandRow = new HorizontalLayout(carIcon, textSection);
        brandRow.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        brandRow.setSpacing(true);
        brandRow.setPadding(false);

        Div brand = new Div(brandRow);
        brand.setWidthFull();
        brand.getStyle()
             .set("padding", "1.5rem 1rem 1rem 1rem")
             .set("border-bottom", "1px solid rgba(255,255,255,0.15)")
             .set("margin-bottom", "0.5rem");
        return brand;
    }

    /**
     * Creates the footer section at the bottom of the sidebar.
     */
    private Div createDrawerFooter() {
        Span version = new Span("v1.0.0  •  Spring Boot + Vaadin");
        version.getStyle()
               .set("color", "rgba(255,255,255,0.45)")
               .set("font-size", "0.68rem")
               .set("text-align", "center")
               .set("display", "block")
               .set("padding", "0.5rem");

        Div footer = new Div(version);
        footer.setWidthFull();
        footer.getStyle().set("border-top", "1px solid rgba(255,255,255,0.12)");
        return footer;
    }

    /**
     * Applies consistent styling to sidebar navigation items.
     *
     * @param item the SideNavItem to style
     */
    private void styleNavItem(SideNavItem item) {
        item.getStyle()
            .set("color", "rgba(255,255,255,0.85)")
            .set("border-radius", "8px")
            .set("margin", "2px 8px");
    }
}
