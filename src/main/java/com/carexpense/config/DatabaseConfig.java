package com.carexpense.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * Database configuration for the Car Expense Tracking System.
 *
 * <p>Configures the Oracle JDBC DataSource using values from
 * application.properties or system environment variables.
 * Credentials are never hardcoded in this class.</p>
 *
 * <p>Required environment variables (or application.properties keys):
 * <ul>
 *   <li>DB_URL      - Oracle JDBC URL, e.g. jdbc:oracle:thin:@localhost:1521/XE</li>
 *   <li>DB_USERNAME - Oracle database username</li>
 *   <li>DB_PASSWORD - Oracle database password</li>
 * </ul>
 * </p>
 */
@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    /**
     * Creates and configures the Oracle DataSource bean.
     *
     * @return configured {@link DataSource} instance
     */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);
        return dataSource;
    }
}
