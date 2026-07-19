package org.blinkapp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;


@Configuration
@ComponentScan
public class ApplicationConfiguration {

    @Value("${DB_URL")
    private String url;

    @Value("${DB_USERNAME}")
    private String username;

    @Value("${DB_PASSWORD")
    private String password;

}
