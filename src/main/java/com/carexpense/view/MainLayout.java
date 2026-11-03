package com.carexpense.view;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;

/**
 * Main application shell — fixed dark navy sidebar and top navbar.
 *
 * Design tokens:
 *   Primary Navy background      : #0B1F4D
 *   Active nav highlight         : #193A70
 *   Navbar text & icons          : #FFFFFF
 *   Sidebar text & icons         : #FFFFFF
 *   Content background           : #F4F6FA
 */
public class MainLayout extends AppLayout {

    private static final String NAVY        = "#0B1F4D";
    private static final String NAVY_ACTIVE = "#193A70";
    private static final String WHITE       = "#FFFFFF";
    private static final String SIDEBAR_W   = "240px";

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        buildNavbar();
        buildSidebar();

        // Inject global styling for perfect sidebar colors, active states, and drawer width
        getElement().executeJs(
            "this.style.setProperty('--vaadin-app-layout-drawer-width', '" + SIDEBAR_W + "');" +
            "document.documentElement.style.setProperty('--vaadin-app-layout-drawer-width', '" + SIDEBAR_W + "');" +
            "if (!document.getElementById('custom-nav-styles')) {" +
            "  const style = document.createElement('style');" +
            "  style.id = 'custom-nav-styles';" +
            "  style.innerHTML = `" +
            "    vaadin-side-nav-item {" +
            "      --vaadin-side-nav-item-color: rgba(255, 255, 255, 0.85);" +
            "      --vaadin-side-nav-item-active-color: #FFFFFF !important;" +
            "      --vaadin-side-nav-item-active-background: " + NAVY_ACTIVE + " !important;" +
            "      color: rgba(255, 255, 255, 0.85);" +
            "    }" +
            "    vaadin-side-nav-item::part(link) {" +
            "      color: rgba(255, 255, 255, 0.85) !important;" +
            "      border-radius: 6px;" +
            "      margin: 2px 4px;" +
            "      transition: background 0.15s ease;" +
            "    }" +
            "    vaadin-side-nav-item::part(link):hover {" +
            "      background: rgba(255, 255, 255, 0.08) !important;" +
            "      color: #FFFFFF !important;" +
            "    }" +
            "    vaadin-side-nav-item[active]::part(link) {" +
            "      background: " + NAVY_ACTIVE + " !important;" +
            "      color: #FFFFFF !important;" +
            "      border-left: 3px solid #FFFFFF !important;" +
            "      font-weight: 600 !important;" +
            "    }" +
            "    vaadin-side-nav-item vaadin-icon {" +
            "      color: inherit !important;" +
            "    }" +
            "  `;" +
            "  document.head.appendChild(style);" +
            "}"
        );
    }

    private void buildNavbar() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getStyle()
              .set("color", WHITE)
              .set("cursor", "pointer")
              .set("margin-right", "0.5rem");

        Span appName = new Span("Car Expense Tracker");
        appName.getStyle()
               .set("color", WHITE)
               .set("font-size", "1.1rem")
               .set("font-weight", "600")
               .set("letter-spacing", "0.2px")
               .set("margin", "0");

        HorizontalLayout navbar = new HorizontalLayout(toggle, appName);
        navbar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        navbar.setSpacing(true);
        navbar.setPadding(false);
        navbar.setWidthFull();
        navbar.setHeight("56px");
        navbar.getStyle()
              .set("background", NAVY)
              .set("padding", "0 1.25rem")
              .set("box-shadow", "0 2px 4px rgba(0,0,0,0.18)");

        addToNavbar(true, navbar);
    }

    private void buildSidebar() {
        Div brand = buildBrand();

        SideNav nav = new SideNav();
        nav.setWidthFull();
        nav.getStyle()
           .set("padding", "0.5rem 0.5rem")
           .set("--vaadin-side-nav-item-color", "rgba(255,255,255,0.85)")
           .set("--vaadin-side-nav-item-active-color", WHITE)
           .set("--vaadin-side-nav-item-active-background", NAVY_ACTIVE);

        SideNavItem dashboardItem = new SideNavItem(
                "Dashboard", DashboardView.class, VaadinIcon.DASHBOARD.create());
        SideNavItem addExpenseItem = new SideNavItem(
                "Add Expense", AddExpenseView.class, VaadinIcon.PLUS_CIRCLE.create());
        SideNavItem viewExpensesItem = new SideNavItem(
                "View Expenses", ViewExpensesView.class, VaadinIcon.LIST.create());
        SideNavItem reportsItem = new SideNavItem(
                "Reports", ReportsView.class, VaadinIcon.BAR_CHART.create());
        SideNavItem aboutItem = new SideNavItem(
                "About", AboutView.class, VaadinIcon.INFO_CIRCLE.create());
        SideNavItem contactItem = new SideNavItem(
                "Contact", ContactView.class, VaadinIcon.ENVELOPE.create());

        styleNavItem(dashboardItem);
        styleNavItem(addExpenseItem);
        styleNavItem(viewExpensesItem);
        styleNavItem(reportsItem);
        styleNavItem(aboutItem);
        styleNavItem(contactItem);

        nav.addItem(dashboardItem, addExpenseItem, viewExpensesItem, reportsItem, aboutItem, contactItem);

        Span version = new Span("v1.0  ·  Spring Boot + Vaadin");
        version.getStyle()
               .set("color", "rgba(255,255,255,0.4)")
               .set("font-size", "0.7rem")
               .set("text-align", "center")
               .set("display", "block")
               .set("padding", "0.85rem 1rem");

        Div footer = new Div(version);
        footer.setWidthFull();
        // Pin footer to bottom naturally using margin-top auto, so navigation stays pinned to TOP
        footer.getStyle()
              .set("margin-top", "auto")
              .set("border-top", "1px solid rgba(255,255,255,0.1)");

        // Top-aligned sidebar container (START, not BETWEEN!)
        VerticalLayout sidebar = new VerticalLayout(brand, nav, footer);
        sidebar.setSizeFull();
        sidebar.setPadding(false);
        sidebar.setSpacing(false);
        sidebar.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        sidebar.getStyle()
               .set("background", NAVY)
               .set("min-height", "100vh")
               .set("width", SIDEBAR_W)
               .set("overflow-x", "hidden");

        addToDrawer(sidebar);
    }

    private Div buildBrand() {
        Icon carIcon = VaadinIcon.CAR.create();
        carIcon.setSize("1.5rem");
        carIcon.getStyle()
               .set("color", WHITE)
               .set("flex-shrink", "0");

        Span name = new Span("CarTracker");
        name.getStyle()
            .set("color", WHITE)
            .set("font-size", "1.15rem")
            .set("font-weight", "700")
            .set("letter-spacing", "0.3px");

        HorizontalLayout row = new HorizontalLayout(carIcon, name);
        row.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        row.setSpacing(true);
        row.setPadding(false);

        Div brand = new Div(row);
        brand.setWidthFull();
        brand.getStyle()
             .set("padding", "1.25rem 1.25rem 1rem 1.25rem")
             .set("border-bottom", "1px solid rgba(255,255,255,0.12)")
             .set("margin-bottom", "0.25rem");
        return brand;
    }

    private void styleNavItem(SideNavItem item) {
        item.getStyle()
            .set("color", "rgba(255,255,255,0.85)")
            .set("border-radius", "6px")
            .set("margin", "2px 0")
            .set("font-size", "0.88rem")
            .set("font-weight", "500");
    }
}
