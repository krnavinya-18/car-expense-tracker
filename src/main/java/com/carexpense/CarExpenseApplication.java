package com.carexpense;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Car Expense Tracking System.
 *
 * <p>This Spring Boot application provides a browser-based interface
 * for tracking car-related expenses using Vaadin Flow for the UI
 * and Oracle Database for data persistence.</p>
 *
 * <p>Run the application and navigate to: http://localhost:8080</p>
 */
@SpringBootApplication
public class CarExpenseApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarExpenseApplication.class, args);
    }
}
