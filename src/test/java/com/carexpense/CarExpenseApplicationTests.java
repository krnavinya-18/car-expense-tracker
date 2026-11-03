package com.carexpense;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Basic Spring Boot context load test.
 */
@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:oracle:thin:@localhost:1521/XE",
        "spring.datasource.username=test",
        "spring.datasource.password=test",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.sql.init.mode=never"
})
class CarExpenseApplicationTests {

    @Test
    void contextLoads() {
        // Verifies that the Spring application context initialises without errors.
    }
}
